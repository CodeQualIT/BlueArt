## Context

The browse and art stylesheets currently define the BlueArt palette as repeated raw values and page-local custom properties in `:app` `CssController`. Kolo already provides a deterministic styling pipeline: typed DSL helpers collect Tailwind-style tokens at render sites, the runtime canonicalizes them into the `kolo.css` request, and parser/generator hooks in `:libs:kolo-styles` produce CSS. Font family utilities already refer to `--font-sans`, `--font-serif`, and `--font-mono`, but no Kolo-owned source defines a complete semantic theme for color and future utility families.

Pending background/border, typography-refinement, SVG, and effects changes need a shared, constrained source of theme values. These values do not belong to a single utility family: text color belongs with typography, background color with backgrounds, and border color, width, and radius with borders. Kolo must define the complete Tailwind default theme-variable baseline, including values for utility families Kolo does not yet implement, while BlueArt owns its application-specific additions and overrides as a layer above that baseline. Without shared infrastructure, each family could encode values independently or accept arbitrary CSS, undermining visual consistency and deterministic generation.

The design must preserve the current BlueArt appearance while `kolo.css` and generated page CSS coexist. It must also preserve the existing canonical token ordering, request URL shape, permissive diagnostics for unsupported utilities, and Spring auto-configuration boundary. Stakeholders are Kolo library maintainers, app UI maintainers, and reviewers responsible for visual parity.

This design uses the proposal for motivation and the existing Kolo utility architecture, CSS-generation, and generated-page-stylesheet specifications as the implementation baseline. The dependent utility changes supply the consumer requirements for theme colors and fonts.

## Goals / Non-Goals

**Goals:**
- Establish `:libs:kolo-styles` as the owner of a reusable default Kolo theme and let `:app` own the BlueArt theme overlay.
- Model the full Tailwind default theme-variable baseline in Kolo, including unsupported-yet namespaces, with BlueArt semantic entries layered on it.
- Define Tailwind-compatible theme custom properties for the BlueArt color roles and supported font families, using stable semantic names rather than raw color values at utility call sites.
- Make each utility generator able to request the exact Kolo theme properties it needs, so the stylesheet emits a deterministic and minimal `:root` block.
- Require typography, background, border, SVG, and effects utility generators to resolve only explicit theme entries, with family-specific typed DSL helpers producing canonical utility tokens.
- Migrate existing page-local theme variables to the Kolo theme incrementally without changing the rendered BlueArt palette.

**Non-Goals:**
- User-selectable themes, per-request theming, dark mode, persistence, or a theme administration UI.
- Embedding BlueArt colors, font choices, semantic roles, or other application-specific design decisions in `:libs:kolo-styles`.
- Arbitrary-value utility syntax, raw CSS color/font input, or runtime configuration loaded from application properties, a database, or a remote service.
- Implementing utility families solely because their corresponding baseline values exist.
- A visual redesign, expansion of the BlueArt palette, or migration of every existing declaration in this change.
- Changes to Kolo token canonicalization, stylesheet URL delivery, caching strategy, or pseudo/responsive variant semantics.

## Decisions

### 1) Keep the default Kolo theme generic and compose consumer overlays outside utility families

**Decision:** Define the default Kolo theme in a dedicated shared theme package in `:libs:kolo-styles`, separate from utility token, parser, and generator packages. It contains an immutable, version-pinned representation of the complete Tailwind default theme-variable reference, translated to Kolo's `--k-*` namespace. It covers every Tailwind namespace and default theme property, including color, font, text, font weight, tracking, leading, tab size, breakpoints, containers, spacing, radius, shadows, inset shadows, drop shadows, blur, perspective, zoom, aspect ratio, easing, animation, and default font/transition settings. It also records animation keyframe definitions associated with baseline animation entries, even where Kolo has no corresponding utility family yet.

The Kolo package exposes typed metadata, a stylesheet-emission collector, and an ordered consumer-theme contribution contract. The app contributes a BlueArt theme overlay through that contract, defining BlueArt-only semantic entries and deliberately overriding baseline entries where BlueArt needs different values. During application startup, Kolo discovers, orders, validates, and composes the default theme with registered consumer contributions into one immutable effective-theme bean. Request-time generators read that fixed bean; they do not discover, compose, or mutate theme configuration. Typography, backgrounds, borders, SVG, and effects consume the effective shared contract; they do not know which module supplied an entry.

**Rationale:** The library owns the generic compiler and DSL contract, but not application branding. A neutral shared package prevents duplicate mappings without assigning cross-cutting theme ownership to typography, backgrounds, or borders. Consumer overlays preserve that boundary while allowing BlueArt and later Kolo consumers to customize the baseline without forking it. Startup composition makes configuration failures deterministic and removes per-request configuration work. Modeling the complete baseline now avoids later utility families inventing incompatible defaults, while keeping unsupported values inert until a generator consumes them.

**Alternatives considered:**
- Keep theme values in `CssController`: rejected because reusable Kolo utilities cannot depend on app-specific endpoint CSS.
- Define separate color maps in every utility family: rejected because aliases and values would drift.
- Place the theme under the typography, background, or border package: rejected because all of those families consume the same values and none owns the shared contract.
- Add baseline values only when a utility family is implemented: rejected because it makes later utilities responsible for defining core theme behavior and creates inconsistent defaults.
- Put BlueArt values in the Kolo default theme: rejected because reusable library defaults must not contain application branding or application-specific semantic roles.
- Load a theme from `application.yml` or a database: rejected because it adds runtime variability and cache invalidation concerns before configurable themes are a requirement.

### 2) Use semantic Tailwind-style CSS custom properties as the public theme contract

**Decision:** Map each Tailwind baseline namespace to the same Kolo-prefixed CSS-property suffix, for example `--k-color-*`, `--k-font-*`, `--k-text-*`, `--k-spacing-*`, `--k-radius-*`, and `--k-shadow-*`. The BlueArt overlay in `:app` may override baseline values and add application-specific semantic color roles under `--k-color-blueart-*`; those roles represent purpose, not a hexadecimal value: page background, surfaces, author surface, borders, primary/action, accent, banner, body text, muted text, hashtag/link text, and the existing light foreground values needed by banner and action treatments.

The Kolo baseline maps entries to the pinned Tailwind reference values; the BlueArt overlay maps application-specific entries and overrides to their current exact values. Theme-aware utilities generate declarations through `var(--k-...)`; they do not embed raw values. Kolo MUST NOT emit unprefixed custom properties. Gradients, alpha composites, and other composite declarations remain page-CSS exceptions until a later utility family defines exact, theme-backed parity.

**Rationale:** Semantic roles make utility intent readable and allow a value to change centrally. CSS properties keep utilities compatible with server-generated CSS and existing browser cascade behavior.

**Alternatives considered:**
- Expose only hue/scale names such as `blue-500`: rejected because the current palette is role-oriented and utilities should not encode implementation colors.
- Have each generated rule contain the resolved hex value: rejected because it duplicates the palette throughout generated output.
- Use only Kotlin values without CSS custom properties: rejected because generated CSS needs a shared browser-visible source and page CSS must coexist during migration.

### 3) Emit only generator-requested theme properties into a shared `:root` block

**Decision:** Theme metadata is not itself a utility token, parser, or generator. During CSS compilation, a supported utility generator requests the specific entry or entries required by the CSS rule it emits from the immutable effective theme created at application startup. The compiler collects those requests and emits one deterministic `:root` block before utility rules. The block contains only requested `--k-*` properties, ordered by baseline then contribution order and independent of request token order. An overlay value replaces the baseline value for the same property; conflicting values from contributions at the same precedence fail application startup rather than relying on discovery order. If a supported generator requests an animation entry, the compiler emits its associated keyframes outside `:root` exactly once. Baseline entries and keyframes for utility families Kolo does not support remain registered metadata only and MUST NOT be emitted. A request with no theme-aware utilities emits no theme block.

**Rationale:** A theme-backed utility remains self-sufficient without adding unused variables, unsupported-family styles, or unused animation artifacts to a stylesheet response. The shared collector centralizes root emission while keeping the existing family-specific parser/generator architecture intact.

**Alternatives considered:**
- Emit the complete baseline in every `kolo.css` response: rejected because most routes use only a subset of theme entries, and unsupported utility families must not add unused root variables or styles.
- Serve a separate static theme stylesheet: rejected because it introduces another delivery path and ordering requirement during migration.
- Keep the variables permanently in browse/art CSS: rejected because it leaves ownership split and makes Kolo non-portable.

### 4) Preserve page compatibility through a bounded alias-and-migration phase

**Decision:** Add the Kolo-owned canonical `--k-*` baseline and the app-owned BlueArt overlay first. Update page CSS and Kolo consumers to reference the effective canonical properties as declarations are migrated. Page CSS must not rely on an on-demand property unless the page also guarantees a Kolo utility requests it; where that guarantee is not yet present, retain the existing page-local value until migration. If preserving an existing page-local property name is necessary during the transition, retain a temporary alias in `CssController` that resolves to the canonical Kolo property only when the associated Kolo utility is present; do not emit legacy aliases from Kolo. Remove the page-CSS alias only after no generated page stylesheet references it.

Each ownership transfer is atomic per declaration: add the matching typed Kolo utility at the render site, remove the duplicate declaration from `CssController`, and retain non-equivalent rules as explicit `kolo-exception`s.

**Rationale:** This preserves cascade behavior and visual parity while avoiding a risky all-at-once conversion. Bounded aliases provide compatibility without creating a second long-term vocabulary.

**Alternatives considered:**
- Rename all custom properties and migrate all color declarations in one release: rejected because it creates unnecessary visual-regression risk.
- Keep legacy and canonical variables indefinitely: rejected because it obscures ownership and encourages new usage of deprecated names.
- Let both page CSS and utilities own a migrated declaration: rejected because precedence can hide drift and make later changes unpredictable.

### 5) Enforce deterministic, allow-listed theme consumption

**Decision:** Each utility family exposes only its own named baseline and semantic entries: typography owns text-color helpers, backgrounds own background-color helpers, and borders own border-color/radius/width helpers. Their parser hooks accept only the documented family grammar and their generators resolve values through the shared theme registry while registering required properties with the shared collector. An entry's presence in the baseline does not make its utility syntax available; it becomes consumable only when its owning utility family implements and allow-lists it. Unknown theme names, malformed tokens, and unsupported property/value combinations follow the existing diagnostic CSS path rather than falling back to raw CSS or a default color.

**Rationale:** The established Kolo architecture prioritizes type safety and deterministic output. Extending that boundary to themes prevents accidental arbitrary CSS injection through token text and keeps generated styles reviewable.

**Alternatives considered:**
- A generic `color(String)` or `theme(String)` DSL: rejected because it blurs property-family ownership and defeats compile-time discoverability and allow-list enforcement.
- Silent fallback to a default theme value: rejected because it makes visual defects difficult to detect.
- Hard-fail the whole stylesheet request: rejected because the compiler contract deliberately preserves supported rules and surfaces diagnostics for bad tokens.

## Risks / Trade-offs

- [Kolo stylesheet ordering could change effective values while page CSS still uses legacy variables] -> Emit canonical properties before utilities, use temporary aliases only where needed, and migrate one declaration at a time with controller and visual coverage.
- [Semantic roles may be too coarse for a later consumer] -> Start with roles evidenced by the existing BlueArt palette; add a role only when a concrete utility family needs a distinct visual meaning.
- [On-demand root emission could omit a property page CSS still expects] -> Keep page CSS self-contained until its matching declaration has migrated, and test each migration route with its final Kolo token set.
- [The copied Tailwind baseline could drift from its source reference] -> Pin the source Tailwind version in the Kolo baseline metadata and add a completeness test that compares every documented namespace/entry to the pinned baseline fixture.
- [Overlay ordering or conflicts could make BlueArt output non-deterministic] -> Use explicit contribution precedence, replace only a baseline value at a declared precedence, and fail startup for same-precedence conflicts.
- [An invalid overlay could prevent the application from starting] -> Validate names, namespaces, values, precedence, and keyframe references while constructing the immutable effective theme, with focused startup-context tests that surface the conflicting contribution.
- [Unsupported baseline values could accidentally become output] -> Limit collector requests to supported generators and test that unsupported-only entries never create a `:root` block or companion styles.
- [A new utility family could bypass the registry and reintroduce raw colors] -> Require shared theme resolution in parser/generator tests and treat direct raw color emission as outside the utility contract.
- [Legacy aliases may become permanent technical debt] -> Inventory aliases in tests and remove each once all generated page CSS references have migrated.

## Migration Plan

1. Import the full Tailwind default theme-variable reference into a version-pinned generic Kolo baseline registry, preserving all namespaces in `--k-*` form even when Kolo does not implement their utilities.
2. Add the ordered consumer-theme contribution contract and compose it with the Kolo baseline once during application startup into an immutable effective-theme bean.
3. Inventory the current `CssController` variables and raw values, map only existing visual roles to an app-owned BlueArt theme overlay, and identify the legacy property names that require temporary aliases.
4. Add the shared on-demand root-property collector to `:libs:kolo-styles`; retain the current `kolo.css` route, canonical token handling, and diagnostics behavior.
5. Add focused startup, compiler, and endpoint tests covering baseline completeness, overlay precedence/conflicts, immutable startup composition, requested-property-only emission, fixed ordering, deduplication across utility families, no output for unsupported-only baseline entries, empty-token responses, and absence of raw-value fallbacks.
6. Update the existing font family definitions to resolve from the effective theme through the typography family and add the family-neutral theme-resolution contract needed by future color-aware utility families.
7. Move page CSS to canonical properties and add only necessary transition aliases. Do not migrate gradients or composite declarations without exact utility parity.
8. Implement dependent utility families against the shared effective theme, then transfer matching declarations per render-site/selector batch and remove duplicates from `CssController`.
9. Validate generated CSS ownership boundaries and visual parity for browse and art pages. Roll back a parity issue by restoring the removed page-CSS declaration and removing the corresponding utility token; the generic baseline and app overlay can remain because unrequested entries are not emitted.

## Open Questions

- Which semantic name should represent the current `--hashtag` value: a general link role shared with other rich-text links, or a hashtag-specific role retained for future independent styling?
- Should the initial theme expose the existing light foreground values as separate semantic roles, or should a later contrast-token convention define them after the first color utility consumers are implemented?
