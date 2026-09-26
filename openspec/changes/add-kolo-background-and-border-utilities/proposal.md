## Why

Browse and art styling still centralizes solid surfaces, borders, outlines, radii, and gradients despite Kolo’s co-located utility architecture.

## What Changes

- Add typed background, border, outline, and radius utilities backed by Kolo theme colors.
- Migrate direct-parity declarations from `CssController`; retain bespoke gradient and alpha-composite values as exceptions.

## Capabilities

### New Capabilities
- `kolo-background-border-ownership`: background and border utility ownership

### Modified Capabilities
- `kolo-utility-architecture`: Extends the typed utility-family contract for this capability.
- `kolo-css-generation`: Extends deterministic parser/generator endpoint output for this capability.
- `kolo-html-runtime-integration`: Extends typed DSL/runtime token collection for this capability.
- `generated-page-stylesheets`: Transfers migrated declarations from page CSS to Kolo.

## Impact

Depends on `add-kolo-theme-management`; affects `:libs:kolo-styles`, page render sites, CssController, and visual baselines.
