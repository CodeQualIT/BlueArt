## MODIFIED Requirements

### Requirement: Kolo utilities own font declarations
The system SHALL define any font-related styling behavior for migrated browse and art elements through `:libs:kolo-styles` utilities, and `CssController` MUST NOT emit those font-related declarations for migrated elements.
Font-family utility generation SHALL resolve supported families through the immutable effective Kolo theme and MUST emit declarations through canonical `var(--k-font-*)` references rather than raw font stacks.

#### Scenario: Generated page styles omit migrated font declarations
- **WHEN** `CssController` generates page CSS for `/css/generated/browse.css` and `/css/generated/art.css`
- **THEN** the generated page CSS contains no font-related declarations for elements that have been migrated to Kolo font utilities

#### Scenario: A font-family utility is generated
- **WHEN** a supported font-family utility is included in a Kolo stylesheet request
- **THEN** its declaration uses the corresponding canonical `var(--k-font-*)` reference and requests that property from the effective theme

### Requirement: Rendering expresses typography through typed Kolo DSL helpers
The system SHALL support typography authoring through typed `kolo { ... }` font helpers for font-related styling, and MUST emit the resulting font tokens through the Kolo stylesheet pipeline.

#### Scenario: Font helpers produce canonicalized utility delivery
- **WHEN** render code applies typed font helpers in `kolo { ... }` on an element
- **THEN** the rendered output includes Kolo utility class names on that element and a `/css/generated/kolo.css` stylesheet reference that includes canonicalized font tokens

### Requirement: Font migration preserves typography parity
The system MUST preserve effective typography behavior for browse and art pages while moving font ownership from `CssController` to Kolo utilities.

#### Scenario: Equivalent typography after ownership transfer
- **WHEN** a font declaration is replaced by an equivalent Kolo font utility and the old `CssController` declaration is removed
- **THEN** the affected rendered page section keeps equivalent font-related styling behavior
