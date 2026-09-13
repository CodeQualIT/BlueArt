# Design: Add Kolo theme color tokens

## Context

BlueArt's browse and art stylesheets each declare an almost identical palette
in `:app` `CssController`. Page CSS currently owns both the raw custom-property
definitions and their visual use, which makes a consistent whole-site theme and
future color utilities difficult to establish.

Kolo already generates request-specific utility CSS through this path:
`kolo { ... }` records utility tokens during `renderKoloHtml`; the runtime
canonicalizes them into a `/css/generated/kolo.css` URL; `KoloCssController`
passes them through typed parser/generator hooks and a shared `CssBuilder`.
Kolo CSS intentionally coexists with generated page CSS during migration.

The theme is not a per-page or runtime-selectable concept. Kolo has one
standard default theme containing its built-in values (including standard color
and font tokens). Each consuming application has at most one startup-configured
theme extension. BlueArt's extension supplies its semantic colors and default
font preferences, then Kolo resolves the defaults plus that extension into one
active theme. `KoloCssController` emits this active theme for every
`kolo.css` response only for semantic roles referenced by requested `k-*`
color utilities. Render sites do not record a theme token or choose a theme.

The initial BlueArt color roles are `bg`, `surface`, `surface-2`,
`author-surface`, `border`, `text`, `muted`, `banner`, `primary`, `primary-2`,
`accent`, and `hashtag`. Although some are presently used only on browse or
art pages, they are global semantic roles available to the whole website.

This design follows the [Tailwind theme model](https://tailwindcss.com/docs/theme):
theme variables define the values from which utility families derive their
available names, and generated variables remain usable from ordinary CSS.
Kolo does not adopt Tailwind's build-time `@theme` directive or its default
numeric palette. Its Kotlin compiler instead emits only needed regular `:root`
`--k-color-*` variables at stylesheet request time.

The proposal provides motivation and scope. The implementation contract is in
the `kolo-theme-colors` specification and the deltas for
`kolo-utility-architecture`, `kolo-css-generation`,
`kolo-html-runtime-integration`, and `generated-page-stylesheets`.

## Goals / Non-Goals

**Goals:**

- Establish one active whole-site Kolo theme consisting of library defaults
  extended by one BlueArt application configuration.
- Move semantic color values and default font preferences to an immutable
  startup configuration for Kolo utility generation.
- Have `KoloCssController` emit only resolved theme variables required by the
  page's requested `k-*` color utilities, without page-level configuration.
- Use `--k-color-*` variables so Kolo ownership aligns with its generated
  `k-*` class prefix, while retaining Tailwind-style utility suffixes for
  future color families.
- Provide a reusable library contract and string-based DSL primitives from
  which an application can build ergonomic custom utility extensions.
- Preserve current visual behavior, URL canonicalization, endpoint paths,
  diagnostics, and side-by-side stylesheet delivery.

**Non-Goals:**

- Supporting multiple named themes, runtime theme selection, dark mode, or
  per-page theme activation.
- Adding background, text, border, fill, stroke, shadow, gradient, opacity, or
  other element-level color utility families in this change.
- Adding arbitrary color values, arbitrary CSS-variable utility syntax, or
  mutable runtime palette registration.
- Replacing BlueArt's palette values, redesigning its visuals, or removing
  color-bearing page declarations before a direct utility equivalent exists.
- Introducing a Tailwind dependency or emitting Tailwind `@theme` syntax.

## Decisions

### 1. Use one resolved active theme, not a theme registry

**Decision:** `:libs:kolo-styles` supplies immutable standard Kolo theme
defaults. `:app` supplies one immutable BlueArt extension at Spring startup.
A theme resolver merges the extension over the defaults exactly once when
application wiring is created, exposing one active `KoloTheme` to Kolo CSS
generation. The application extension may add semantic color roles and
override default values such as font preferences, but it cannot select among
multiple themes.

**Rationale:** BlueArt needs one consistent visual system, not tenant, user, or
page-specific theming. One resolved object makes the active design-token set
easy to audit, prevents conflicting cascade sources, and gives future utility
families one deterministic allow-list.

**Alternatives considered:**

- A registry of named themes with a `theme-blueart` activation token: rejected
  because it adds unused selection behavior and requires every page to opt in.
- Keep all theme values in `CssController`: rejected because page CSS cannot be
  the shared authority for library-generated utilities or future pages.
- Hardcode BlueArt values in `:libs:kolo-styles`: rejected because the library
  must remain reusable by another application.

### 2. Configure the BlueArt extension through Spring startup wiring

**Decision:** Add an application-owned Spring `@Configuration` that creates the
single BlueArt theme extension bean. Kolo's theme resolver and
`KoloCssController` receive the resolved active theme through normal dependency
injection. Configuration is immutable after startup and contains no external
request, user, or page input.

**Rationale:** The palette and default font preferences are application
configuration, just like other startup-owned implementation choices. Spring
wiring gives `KoloCssController` its configuration directly, avoids global
state, and lets tests replace or construct an explicit theme definition.

**Alternatives considered:**

- Supply the theme name through every rendered page's token list: rejected
  because the active theme is not a render-site choice.
- Put values in an endpoint query parameter: rejected because it weakens
  caching, validation, and whole-site consistency.
- Load a mutable map from application code on every request: rejected because
  it makes output depend on runtime state and complicates deterministic CSS.

### 3. Emit only variables referenced by requested color utilities

**Decision:** The compiler derives the set of referenced semantic roles from
successfully parsed color-utility tokens in a canonical request. Before
emitting those utility rules, it emits one deterministic `:root` block
containing exactly the corresponding `--k-color-*` variables. A request with
no recognized color utility emits no color variables. The active theme remains
startup configured and is never selected by a page.

**Rationale:** Kolo stylesheets are already demand-generated from the
canonical `k-*` class list. Making theme variable emission demand-generated
uses the same delivery model and avoids transmitting palette values unused by
the rendered page. A pre-generation collection pass produces one deduplicated
`:root` rule rather than repeating a variable for every utility.

**Alternatives considered:**

- Emit every active-theme variable on every `kolo.css` response: rejected
  because it transmits unused values and diverges from Kolo's demand-generated
  utility CSS model.
- Emit variables only when a `theme-*` token is requested: rejected because
  theme selection is not a page responsibility.
- Serve a second static theme stylesheet: rejected because it splits versioning,
  caching, and stylesheet ownership from Kolo.
- Emit a separate `:root` rule per matching utility: rejected because it
  duplicates CSS and makes output ordering needlessly sensitive to token order.

### 4. Use a shared, constrained semantic color lookup

**Decision:** The resolved active theme exposes a read-only lookup of semantic
color suffixes to CSS values. BlueArt's palette emits `--k-color-bg`,
`--k-color-surface`, and corresponding variables for every configured role.
Future color utility parser hooks accept only configured role suffixes and
resolve their declaration value as `var(--k-color-<role>)`; they do not contain
their own copy of BlueArt values.

**Rationale:** Tailwind theme variable namespaces make token names determine
which corresponding utilities can exist. Kolo uses the same relationship with a
Kolo prefix to identify ownership, preserving a constrained, deterministic
utility surface while allowing normal page CSS to use the variables directly.

**Alternatives considered:**

- Permit arbitrary `bg-[#...]` or `text-[...]` values: rejected because this
  bypasses the global palette and existing Kolo arbitrary-token restrictions.
- Make each future utility family own its own color map: rejected because
  families would drift and no longer represent one website theme.
- Use an unprefixed `--color-*` namespace: rejected because `--k-color-*`
  clearly identifies Kolo-owned variables alongside `k-*` classes.

### 5. Provide generic string DSL primitives; applications own custom wrappers

**Decision:** Kolo exposes public, string-valued token-recording primitives on
`KoloScope` and `KoloVariantScope`, consistent with the existing base-token and
variant mechanisms. Future property-specific utility families can expose
generic string helpers (for example a background helper accepting a configured
color role). BlueArt may wrap those helpers in local typed extension properties
for its selected roles. Kolo's library-level typed DSL remains appropriate only
for standard Kolo defaults whose names are known when the library is built.

**Rationale:** A reusable library cannot statically generate typed helper names
for roles that an application defines at startup. String primitives keep custom
DSLs ergonomic and composable, while the compiler still validates all resulting
tokens against the one resolved theme.

**Alternatives considered:**

- Hardcode a typed library helper for every application color role: rejected
  because custom role names are unknown to the library.
- Expose only raw internal `recordBase` APIs: rejected because application
  extensions need a supported public API.
- Allow public mutable token or palette registration: rejected because it would
  make the accepted utility set non-deterministic.

### 6. Preserve page-CSS palette definitions until a color utility uses a role

**Decision:** Keep the existing `CssController` root palette definitions and
all color-bearing declarations unchanged in this change. They cannot depend on
Kolo's demand-generated variables because no element-level color utility is
being introduced yet. A later color-utility capability transfers a declaration
only when its `k-*` class references the semantic role; it then removes the
matching page-CSS declaration and any no-longer-needed legacy root variable.

**Rationale:** Retaining current page CSS preserves behavior while this change
supplies configuration and lookup infrastructure. It also prevents page CSS
from depending on a variable that Kolo correctly omits when no `k-*` class uses
it. Color ownership can migrate safely, property family by property family,
once a class directly establishes demand for the variable.

**Alternatives considered:**

- Convert all current color declarations to utilities immediately: rejected
  because this change intentionally does not define those property families.
- Remove page-CSS palette definitions now: rejected because no color utility
  token exists yet to cause Kolo to emit their replacements.

## Risks / Trade-offs

- [A color utility is generated without its referenced variable] → Mitigation:
  parse all request tokens before generation, collect each typed color role,
  emit the deduplicated variable block first, and test mixed color utilities.
- [The application extension overrides a default with an invalid role or value]
  → Mitigation: validate the immutable extension during Spring bean creation
  and fail application startup with a precise configuration error.
- [Theme output varies by request] → Mitigation: resolve immutable defaults and
  the single extension at startup; never derive palette values from requests.
- [Future utility families duplicate palette mappings] → Mitigation: require
  parser hooks to resolve suffixes through the active theme lookup.
- [A public string DSL records an invalid custom token] → Mitigation: preserve
  Kolo's permissive endpoint diagnostics for unsupported tokens and test
  application wrapper tokens against the configured lookup.
- [A future migration removes a page-CSS variable still used by another
  selector] → Mitigation: inventory variable consumers per stylesheet and
  remove a legacy root definition only after its last page-CSS use is gone.

## Migration Plan

1. Inventory the existing `CssController` palette and default font variables;
   freeze their global semantic roles and exact current values.
2. Add immutable Kolo default-theme, application-extension, resolver, and
   read-only lookup contracts in `:libs:kolo-styles`.
3. Add BlueArt Spring configuration in `:app` that creates the one application
   theme extension with BlueArt colors and default font preferences.
4. Inject the resolved active theme into `KoloCssController`/compiler wiring.
   Extend the compiler contract so future typed color tokens expose their
   semantic role; collect those roles before rule generation and emit one
   deterministic, deduplicated `:root` `--k-color-*` block only when at least
   one requested utility uses a role.
5. Expose the supported public string token-recording primitives on Kolo scopes,
   preserving current typed helpers and variants. Do not add element color
   utility helpers until their dedicated capability is designed.
6. Leave browse/art page-CSS palette definitions unchanged. Defer converting
   their declarations or removing legacy root variables to the dedicated
   color-utility migration that first uses each semantic role.
7. Add tests for startup configuration validation, default-plus-extension
   resolution, empty-color-use output, deduplicated deterministic used-variable
   emission, endpoint behavior, and custom DSL token collection.
8. Deploy with the existing `kolo.css` plus page-CSS delivery. Roll back by
   removing the theme wiring; because this change does not transfer page-CSS
   color ownership, no stylesheet restoration is required.
