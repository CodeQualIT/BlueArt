# Decisions

## Purpose
Track technical decisions and rationale in one place. Use this file for concise entries, or add detailed ADRs under `docs/adr/` when needed.

## Decision Log

### D-001: Keep server-rendered UI
- Status: accepted
- Context: Project is focused on fast iteration for browse/detail pages.
- Decision: Render pages server-side with `kotlinx.html` instead of introducing an SPA framework.
- Consequences: Lower frontend complexity, but fewer client-side interaction patterns out of the box.

### D-002: Inline SVG for stat icons
- Status: accepted
- Context: Stat rows require consistent icon rendering across environments.
- Decision: Use inline SVG icons in HTML components.
- Consequences: Deterministic rendering and no emoji/font dependency.

### D-003: URI-first art detail lookup
- Status: accepted
- Context: Post detail requests are most reliable with URI identity.
- Decision: Resolve `/art/{cid}` primarily via provided `uri`, with CID-only fallback as best-effort.
- Consequences: More stable detail fetch when URI is present; CID-only routes may miss off-timeline posts.

### D-004: Track AI tasks in repository docs
- Status: accepted
- Context: GitHub Issues are reserved for human planning and ownership, but AI work requires versioned status and completion traceability.
- Decision: Introduce `docs/AI_TASKS.md` as an index and maintain per-task records under `docs/ai-tasks/`.
- Consequences: AI progress is reviewable in Git history; contributors must keep task records updated as part of handoff.

### D-005: Rename package and artifact from `poc`/`atproto-poc` to `blueart`
- Status: accepted
- Context: The project has grown beyond a proof-of-concept and carries enough real capability to be treated as a real web application. The old group `com.github.cc007.poc.atproto` and artifact `atproto-poc` signalled throwaway/experimental intent.
- Decision: Rename the root package to `com.github.cc007.blueart`, the Gradle artifact description to `blueart`, the Spring application name to `blueart`, and the main entry-point class to `BlueArtApplication`. Remove all "proof-of-concept" prose from documentation.
- Consequences: Any external tooling or CI that references the old artifact name or package must be updated (see BA-002 follow-ups). Build and tests confirmed passing after rename.

### D-006: Adopt a minimal-diff multi-module Gradle structure
- Status: accepted
- Context: Upcoming styling platform work requires reusable library modules without coupling everything to the executable app module.
- Decision: Split the build into `:app` (Spring Boot executable) and `:libs` (library group anchor) while preserving existing app code, package names, routes, and root-level build/test workflows.
- Consequences: Future reusable modules can be added under `:libs:*` without another structural migration; application code now lives under `app/src/*` and should be targeted with `:app:*` tasks when module-specific execution is needed.

### D-007: Adopt Kolo co-located utility styling contract
- Status: accepted
- Context: Styling needs a Kotlin-first co-located authoring flow while preserving maintainability, pseudo/media support, and predictable CSS delivery/caching.
- Decision:
  - Author utilities via element-attached `kolo { ... }` DSL.
  - Use Tailwind-style token naming with pseudo/media variants.
  - Defer arbitrary value tokens (`[...]`) for now.
  - Serve generated utility CSS from `/css/generated/kolo.css` using `version=<build git sha>` and `kolo=<semicolon-separated canonical token list>` query params.
  - Canonicalize `kolo` with dedupe + variant-aware deterministic sorting before link emission.
  - During migration, keep page CSS and `kolo.css` side-by-side; remove only declarations already migrated to Kolo utilities.
  - Use versioned URL caching as the primary cache strategy (`ETag` optional).
- Consequences: Implementation tasks must preserve canonical token ordering and URL stability, and migration work should be incremental to avoid regressions while both stylesheet paths coexist.

### D-008: Move browse/art spacing ownership to Kolo utilities with max-width exceptions
- Status: accepted
- Context: Browse/art spacing migration removed most margin/padding declarations from `CssController`, but two responsive spacing rules still rely on `@media (max-width: 700px)` while Kolo currently supports min-width media variants only.
- Decision:
  - Add `mx-auto` as a first-class spacing utility token and DSL helper (`mxAuto`) for centering cases previously expressed as `margin: 0 auto`.
  - Author migrated browse/art spacing via typed Kolo helpers at render call sites.
  - Keep only the two max-width responsive spacing declarations in `CssController` as explicitly tagged `kolo-exception` rules until Kolo gains max-width variant support.
- Consequences: Spacing ownership is clearer and co-located for migrated elements, and future cleanup can target only the documented responsive exceptions when max-width variants are implemented.

### D-009: Add a dedicated Playwright visual regression lane
- Status: accepted
- Context: CSS-to-Kolo migration needs deterministic visual regression coverage on key user-facing routes without introducing a JavaScript test stack.
- Decision:
  - Add dedicated `:visual-tests` module for black-box visual tests.
  - Run snapshots in headless Chromium and Firefox with normalized rendering settings.
  - Keep visual execution as a separate Gradle lane (`visualTest`) with explicit developer-gated baseline updates (`updateVisualBaselines`).
- Consequences: Visual regressions now emit expected/actual/diff artifacts for route-level review, and baseline updates require explicit developer acknowledgement.

### D-010: Add allow-listed Kolo display utilities and migrate browse/art display ownership
- Status: accepted
- Context: Display declarations for browse/art were still split between page CSS and Kolo tokenized utility rendering, unlike spacing ownership that already migrated into Kolo.
- Decision:
  - Add a dedicated display utility family in `:libs:kolo-styles` via `DisplayToken`, `DisplayParserHook`, and `DisplayGeneratorHook`.
  - Freeze the explicit Tailwind-compatible display allow-list (`block`, `inline`, `inline-block`, `flow-root`, `flex`, `inline-flex`, `grid`, `inline-grid`, `contents`, `list-item`, `hidden`, `table`, `inline-table`, `table-caption`, `table-cell`, `table-column`, `table-column-group`, `table-header-group`, `table-row-group`, `table-row`, `table-footer-group`).
  - Add typed Kotlin display DSL helpers under `kolostyles.dsl.display` on both `KoloScope` and `KoloVariantScope`, including one-to-one camelCase mappings for hyphenated tokens (for example `inlineBlock -> inline-block`, `flowRoot -> flow-root`, `tableRowGroup -> table-row-group`).
  - Reuse existing state/media variant semantics (`hover`, `focus`, `focus-visible`, `active`, `visited`, `sm`/`md`/`lg`/`xl`/`2xl`) across spacing and display parser/generator families.
  - Migrate browse/art render paths to display DSL usage and remove duplicate `display` declarations from generated page CSS for migrated selectors.
- Consequences: Display layout intent is now co-located with server-rendered markup through typed Kolo APIs, `/css/generated/kolo.css` supports both spacing and display utilities through the same deterministic hook pipeline, and browse/art page CSS remains focused on non-migrated declarations.

### D-011: Add allow-listed Kolo font utilities and migrate typography ownership incrementally
- Status: accepted
- Context: Typography declarations in browse/art were still primarily owned by `CssController`, while Kolo already owned spacing/display migration via typed utility hooks.
- Decision:
  - Add a dedicated font utility family in `:libs:kolo-styles` via `FontParserHook`, `FontGeneratorHook`, and typed font token models.
  - Freeze allow-lists to Tailwind-compatible font families (`font-sans`, `font-serif`, `font-mono`), font sizes (`text-xs` through `text-9xl`), and font weights (`font-thin` through `font-black`).
  - Add typed Kotlin font DSL helpers under `kolostyles.dsl.font` for both `KoloScope` and `KoloVariantScope`, with one-to-one token mappings (for example `fontSemiBold -> font-semibold`, `text2xl -> text-2xl`).
  - Keep permissive unsupported/unparsed diagnostics behavior and existing variant/media semantics unchanged.
  - Redefine the app default font baseline through `--font-sans`; migrate only selectors that have direct utility parity and keep unmatched sizes in page CSS as explicit temporary exceptions.
- Consequences: Font utility intent is now co-located with render elements for migrated selectors, `/css/generated/kolo.css` supports spacing + display + font tokens through one deterministic pipeline, and typography migration can proceed safely in small batches without changing stylesheet delivery topology.

### D-012: Add allow-listed Kolo sizing utilities and migrate mappable sizing ownership incrementally
- Status: accepted
- Context: Width/height/min/max sizing declarations for browse/art were still split between `CssController` and render call sites, unlike utility families already migrated into Kolo.
- Decision:
  - Add a dedicated sizing utility family in `:libs:kolo-styles` via `SizingToken`, `SizingParserHook`, and `SizingGeneratorHook`.
  - Consolidate compiler `MediaVariant` into a shared type reused by spacing/display/font/sizing utility families.
  - Freeze an explicit sizing allow-list for `w-*`, `h-*`, `min-w-*`, `max-w-*`, `min-h-*`, `max-h-*`, and `size-*` tokens (named values, Tailwind numeric scale, max-width breakpoints, and supported fractions).
  - Add typed Kotlin sizing DSL helpers under `kolostyles.dsl.sizing` for both `KoloScope` and `KoloVariantScope`.
  - Migrate only Tailwind-mappable sizing declarations to Kolo DSL and keep arbitrary/css-var or selector-context sizing declarations in page CSS with explicit `kolo-exception` markers.
- Consequences: `/css/generated/kolo.css` now supports spacing + display + font + sizing families through one deterministic hook pipeline, and sizing ownership can continue migrating safely in small steps while preserving layout parity.

### D-013: Reclassify display into layout and migrate non-display layout ownership to Kolo
- Status: accepted
- Context: Browse/art still depended on `CssController` for layout declarations (`overflow`, `position`, offsets, `z-index`, `object-fit`, plus global/exceptional layout rules) while display utilities were separated under non-layout package taxonomy.
- Decision:
  - Keep display behavior unchanged but relocate display parser/generator/DSL ownership under `compiler.layout.display` and `kolostyles.dsl.layout.display`.
  - Add non-display layout parser/generator support under `compiler.layout` for `box-sizing`, `overflow`, `position`, `z-index` (`z-<n>` positive integer + `z-auto`), and `object-fit`, with offset parsing/generation (`inset/top/right/bottom/left`) split into dedicated hooks under `compiler.layout.offset`.
  - Add typed layout DSL helpers under `kolostyles.dsl.layout` plus grouped offset helpers in `kolostyles.dsl.layout.offset` for both base and variant scopes.
  - Centralize media/state variant selector emission in shared `StyleGeneratorHook` helper and migrate spacing/display/font/sizing/layout generators to the shared path.
  - Migrate mappable browse/art layout ownership to render-site Kolo helpers and keep non-mappable/max-width layout rules in `CssController` as explicit `kolo-exception`s.
- Consequences: Layout ownership is co-located for migrated selectors, compiler/generator logic has less media-wrapper duplication, and residual page-CSS layout behavior remains auditable until max-width/arbitrary patterns gain utility support.

### D-014: Add directional named Kolo responsive variants
- Status: accepted
- Context: Kolo min-width variants could not express responsive utility ownership below a breakpoint.
- Decision: Share a responsive metadata allow-list across utility parsers, retaining min-width `sm` through `2xl` and adding exclusive max-width `max-sm` through `max-2xl` at fixed Tailwind-compatible boundaries. Keep the legacy 640px, 700px, and 960px page-CSS rules as `kolo-exception`s because no named exclusive range is exactly equivalent.
- Consequences: All current utility families support the same responsive variants and directional CSS emission, while page CSS remains the authoritative owner of intentionally non-equivalent responsive behavior.
