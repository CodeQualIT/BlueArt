## MODIFIED Requirements

### Requirement: Kolo utilities own font declarations
The system SHALL define any font-related styling behavior for migrated browse and art elements through `:libs:kolo-styles` utilities, and `CssController` MUST NOT emit those font-related declarations for migrated elements.
Font-family, font-size, and font-weight utility generation SHALL resolve supported entries through the immutable effective Kolo theme and MUST emit declarations through canonical `var(--k-font-*)`, `var(--k-text-*)`, and `var(--k-font-weight-*)` references rather than raw font values.
Font-family names SHALL be derived from registered `--k-font-*` entries with lowercase hyphen-separated names, excluding reserved weight utility names, the `weight-*` namespace, and double-hyphen font metadata suffixes.
Font-size syntax SHALL retain the existing named size allow-list; font-weight support SHALL be derived from registered `--k-font-weight-*` entries with lowercase alphanumeric, hyphen-separated names, including numeric names only when explicitly registered. Missing effective-theme entries MUST follow unsupported-token diagnostics.
Eligible font-family and font-weight entries that produce the same utility token MUST fail parser initialization at application startup with an ambiguity diagnostic.
The baseline SHALL define all supported font-size entries followed immediately by all corresponding `--k-text-*--line-height` entries in matching size order. Font-size generation SHALL derive, request, and emit that companion property directly from the size property without additional token metadata.
The generator SHALL use typed `kotlinx.css` properties for font-size, font-weight, and line-height declarations.

#### Scenario: Generated page styles omit migrated font declarations
- **WHEN** `CssController` generates page CSS for `/css/generated/browse.css` and `/css/generated/art.css`
- **THEN** the generated page CSS contains no font-related declarations for elements that have been migrated to Kolo font utilities

#### Scenario: A font-family utility is generated
- **WHEN** a supported font-family utility is included in a Kolo stylesheet request
- **THEN** its declaration uses the corresponding canonical `var(--k-font-*)` reference and requests that property from the effective theme

#### Scenario: A consumer contributes a named font family
- **WHEN** a contribution registers `--k-font-display` and a stylesheet requests `font-display`
- **THEN** the compiler emits its effective value once in the shared root block and generates `font-family: var(--k-font-display)`

#### Scenario: A consumer overrides a supported font size or weight
- **WHEN** a contribution overrides `--k-text-2xl` or `--k-font-weight-bold` and the corresponding utility is requested
- **THEN** the shared root block uses the override and the utility references that property without embedding a literal value

#### Scenario: A consumer contributes a custom font weight
- **WHEN** a contribution registers `--k-font-weight-book` and a stylesheet requests `hover:font-book`
- **THEN** the compiler emits the effective weight once in the root block and generates a hover rule referencing `var(--k-font-weight-book)`

#### Scenario: A font family conflicts with a custom weight
- **WHEN** the effective theme registers both `--k-font-book` and `--k-font-weight-book`
- **THEN** application startup fails with a diagnostic identifying the ambiguous `font-book` utility

#### Scenario: A font size has a companion line-height
- **WHEN** `text-base` is requested and `--k-text-base--line-height` is registered
- **THEN** the compiler emits both requested properties once and the utility references them through font-size and line-height declarations

#### Scenario: A consumer overrides only the font size
- **WHEN** a contribution overrides a supported size without overriding its companion line-height
- **THEN** the utility emits the overridden font size and retains the baseline companion line-height

#### Scenario: A font metadata property is not a font family
- **WHEN** a request names a `font-weight-*` property or a double-hyphen font metadata suffix as a family utility
- **THEN** the font parser rejects it and the compiler emits unsupported-token diagnostics without a font-family declaration

### Requirement: Rendering expresses typography through typed Kolo DSL helpers
The system SHALL support typography authoring through typed `kolo { ... }` font helpers for font-related styling, and MUST emit the resulting font tokens through the Kolo stylesheet pipeline.
The font DSL SHALL support named contributed families through `fontFamily(FontFamily(name))` on base and variant scopes while preserving existing named helpers.
The font DSL SHALL support contributed weights through `fontWeight(FontWeight(name))` on base and variant scopes while preserving existing named weight helpers.
`FontWeight` MUST reject raw CSS values and malformed names, and the parser MUST accept only weights registered in the effective theme.
`FontFamily` MUST reject raw CSS values, malformed names, and reserved weight names; the parser MUST accept only families registered in the effective theme.

#### Scenario: Font helpers produce canonicalized utility delivery
- **WHEN** render code applies typed font helpers in `kolo { ... }` on an element
- **THEN** the rendered output includes Kolo utility class names on that element and a `/css/generated/kolo.css` stylesheet reference that includes canonicalized font tokens

### Requirement: Font migration preserves typography parity
The system MUST preserve effective typography behavior for browse and art pages while moving font ownership from `CssController` to Kolo utilities.
The subsequent introduction of baseline companion line-heights SHALL intentionally apply to existing font-size utilities, including art-page headings using `text-base`, without automatically refreshing visual snapshot baselines.

#### Scenario: Equivalent typography after ownership transfer
- **WHEN** a font declaration is replaced by an equivalent Kolo font utility and the old `CssController` declaration is removed
- **THEN** the affected rendered page section keeps equivalent font-related styling behavior
