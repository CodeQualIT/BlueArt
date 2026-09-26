## Why

The page CSS owns a repeated BlueArt palette as raw CSS variables, preventing color, font and other utilities from using a constrained reusable theme.

## What Changes

- Define semantic BlueArt theming compatible with Tailwind-style utility generation (see: https://tailwindcss.com/docs/theme).
- Establish theme ownership and deterministic use by later color, font and other utility families.

## Capabilities

### New Capabilities
TODO

### Modified Capabilities
TODO

## Impact

Affects theme infrastructure and future background, border, typography, SVG, and effects utilities.
