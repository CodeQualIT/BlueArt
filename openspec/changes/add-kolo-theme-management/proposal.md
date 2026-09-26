## Why

The page CSS owns a repeated BlueArt palette as raw CSS variables, preventing color, font and other utilities from using a constrained reusable theme.

## What Changes

- Define semantic BlueArt theming compatible with Tailwind-style utility generation (see: https://tailwindcss.com/docs/theme).
- Establish theme ownership and deterministic use by later color, font and other utility families.

## Capabilities

### New Capabilities
- `kolo-theme-management`: reusable default themes, ordered consumer overlays, and deterministic theme-property emission

### Modified Capabilities
- `kolo-utility-architecture`: Extends the utility contract with allow-listed theme resolution.
- `kolo-css-generation`: Extends deterministic CSS generation with requested theme-property and keyframe output.
- `kolo-html-runtime-integration`: Extends typed DSL/runtime APIs for theme-aware utility families.
- `generated-page-stylesheets`: Defines page-CSS coexistence while theme values migrate to Kolo.
- `kolo-font-ownership`: Moves font-family value resolution to the shared Kolo theme.

## Impact

Affects theme infrastructure and future background, border, typography, SVG, and effects utilities.
