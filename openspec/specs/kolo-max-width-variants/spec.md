# Kolo Max Width Variants

## Purpose

## Requirements

### Requirement: Kolo supports named exclusive max-width responsive variants
The system SHALL support the Tailwind-compatible responsive variants `max-sm`, `max-md`, `max-lg`, `max-xl`, and `max-2xl` for every supported Kolo utility family. The variants MUST generate deterministic exclusive media queries of `width < 40rem`, `width < 48rem`, `width < 64rem`, `width < 80rem`, and `width < 96rem`, respectively. The system MUST preserve the existing `sm` through `2xl` minimum-width variant behavior.

#### Scenario: A max-width variant qualifies a supported utility
- **WHEN** a request includes `max-sm:p-4`
- **THEN** the generated stylesheet emits the `p-4` rule inside `@media (width < 40rem)`

#### Scenario: Each named maximum breakpoint is requested
- **WHEN** a request includes supported utilities qualified by `max-md`, `max-lg`, `max-xl`, and `max-2xl`
- **THEN** each utility is emitted in a maximum-width media query with its corresponding deterministic exclusive boundary

#### Scenario: Existing minimum-width variant is requested
- **WHEN** a request includes a supported utility qualified by an existing `sm` through `2xl` variant
- **THEN** the utility is emitted in its corresponding `@media (width >= Nrem)` query

### Requirement: Kolo composes max-width variants with state and nested variants
The system SHALL allow a max-width responsive variant to compose with supported state variants and nested variant scopes. Composed variants MUST preserve the canonical raw token, escaped utility selector, pseudo-class selector semantics, and direction-specific media query.

#### Scenario: A state variant precedes a max-width variant
- **WHEN** a request includes `hover:max-md:flex`
- **THEN** the generated stylesheet emits the escaped utility selector with the `:hover` pseudo-class inside `@media (width < 48rem)`

#### Scenario: A max-width variant precedes a state variant
- **WHEN** render code chains a `max-md` variant scope with a `hover` variant scope for a supported utility
- **THEN** the rendered token is canonicalized and the generated rule has the equivalent maximum-width media query and hover selector

### Requirement: Unsupported responsive variants retain compiler diagnostics
The system MUST reject responsive variant names outside the supported minimum-width, maximum-width, and state variant allow-lists. Rejected variants MUST NOT generate utility CSS and MUST retain the existing deterministic unparsed-token diagnostic behavior.

#### Scenario: An unsupported maximum variant is requested
- **WHEN** a request includes `max-700:p-4`
- **THEN** no spacing utility rule is emitted and the response includes the deterministic unparsed-token diagnostic for that token
