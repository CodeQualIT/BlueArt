## 1. Responsive Variant Foundation

- [x] 1.1 Define shared responsive variant metadata for existing `sm` through `2xl` minimum-width variants and new `max-sm` through `max-2xl` maximum-width variants, including canonical names, directions, and fixed CSS boundaries.
- [x] 1.2 Set the exclusive maximum-width boundaries to `40rem`, `48rem`, `64rem`, `80rem`, and `96rem` for `max-sm` through `max-2xl`.
- [x] 1.3 Refactor all existing utility parser hooks to consume the shared responsive variant metadata without changing accepted state/minimum-width variants or existing typed token contracts.

## 2. Compiler and CSS Generation

- [x] 2.1 Extend typed compiler token variant metadata so parsed maximum-width variants retain their canonical name, direction, and exclusive boundary.
- [x] 2.2 Update shared variant-rule generation to emit direction-aware `min-width` or exclusive `width <` media queries while retaining escaped selectors and pseudo-state composition.
- [x] 2.3 Verify every registered utility family (spacing, display, non-display layout, offsets, font, and sizing) accepts supported `max-*` variants and rejects unsupported responsive variant names through the existing deterministic diagnostic path.
- [x] 2.4 Preserve canonical request-token ordering, URL serialization, endpoint paths, and permissive unparsed/unsupported diagnostic output for mixed minimum-width, maximum-width, and state-variant requests.

## 3. DSL and Runtime Integration

- [x] 3.1 Add typed, discoverable maximum-width variant-scope helpers for `max-sm`, `max-md`, `max-lg`, `max-xl`, and `max-2xl` on both `KoloScope` and `KoloVariantScope`.
- [x] 3.2 Ensure maximum-width helpers and direct `variant(name)` chaining record identical canonical raw tokens for base, nested, and state-composed utilities.
- [x] 3.3 Add runtime coverage proving rendered classes and finalized `kolo.css` hrefs preserve canonical maximum-width and state/maximum-width variant chains.

## 4. Responsive Ownership Migration

- [x] 4.1 Inventory every browse and art maximum-width declaration, its render element, utility mapping, and breakpoint equivalence before changing stylesheet ownership.
- [x] 4.2 Retain the browse `max-width: 960px` grid/sidebar declarations, art `max-width: 700px` padding declarations, and inclusive browse `max-width: 640px` content-top declarations in `CssController` because no exact named exclusive maximum-width equivalent exists.
- [x] 4.3 Mark each retained non-equivalent responsive declaration with a `kolo-exception` marker and add ownership assertions that page CSS continues to supply it without duplicate Kolo utility ownership.
- [x] 4.4 Keep dual `kolo.css` and generated browse/art stylesheet delivery unchanged during the migration boundary cleanup.

## 5. Test Coverage and Documentation

- [x] 5.1 Add parser and generator tests covering every accepted `max-*` variant boundary, unsupported `max-700` diagnostics, preserved minimum-width output, and state/max variant composition.
- [x] 5.2 Add compiler and controller integration tests for deterministic mixed-family maximum-width CSS output, escaped selectors, and permissive endpoint diagnostics.
- [x] 5.3 Extend `CssControllerTest` and Playwright visual regression coverage for browse and art at the `640px`, `700px`, and `960px` legacy responsive boundaries.
- [x] 5.4 Update architecture, decision, testing, and glossary documentation to describe directional responsive variants and the retained `kolo-exception` ownership boundary.
- [x] 5.5 Run the relevant Gradle test suites, `./gradlew visualTest`, and `openspec validate --all`, resolving all reported failures before handoff.
