## Why

Kolo supports only mobile-first min-width variants, leaving three responsive page-CSS exceptions and blocking utility ownership.

## What Changes

- Add Tailwind-compatible `max-*` breakpoint variants to Kolo.
- Migrate the 960px browse sidebar, 700px art spacing, and 640px content-top overrides where equivalent utilities exist.

## Capabilities

### New Capabilities
- `kolo-max-width-variants`: max-width responsive variants

### Modified Capabilities
- `kolo-utility-architecture`: Extends the typed utility-family contract for this capability.
- `kolo-css-generation`: Extends deterministic parser/generator endpoint output for this capability.
- `kolo-html-runtime-integration`: Extends typed DSL/runtime token collection for this capability.
- `generated-page-stylesheets`: Transfers migrated declarations from page CSS to Kolo.

## Impact

Affects shared variant parsing/generation, `:libs:kolo-styles`, browse/art render sites, `CssController`, and responsive visual coverage.
