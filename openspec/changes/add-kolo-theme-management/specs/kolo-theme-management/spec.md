## ADDED Requirements

### Requirement: Kolo composes a generic baseline theme with ordered consumer overlays
The system SHALL define a dedicated theme package in `:libs:kolo-styles` that owns an immutable, version-pinned representation of the complete Tailwind default theme-variable baseline in the `--k-*` namespace, including metadata and animation keyframes for namespaces that Kolo does not yet support.
The baseline MUST NOT contain BlueArt colors, fonts, semantic roles, or other application-specific decisions.
The system SHALL expose a typed, ordered consumer-theme contribution contract, and SHALL compose the baseline with registered contributions once during application startup into an immutable effective-theme bean.
The `:app` module SHALL contribute the BlueArt overlay through that contract.
Contributions SHALL be validated for property names, namespaces, values, precedence, and keyframe references; same-precedence conflicting values for the same property MUST fail application startup.

#### Scenario: Application startup composes the BlueArt theme
- **WHEN** the application starts with the default Kolo baseline and the BlueArt overlay contribution
- **THEN** generators receive one immutable effective theme containing the generic baseline plus the BlueArt semantic entries and declared overrides

#### Scenario: Same-precedence contributions conflict
- **WHEN** two consumer contributions at the same precedence provide different values for the same theme property
- **THEN** application startup fails with a diagnostic that identifies the conflicting property and contributions

#### Scenario: Unsupported baseline namespace is registered
- **WHEN** the baseline contains a Tailwind namespace for which Kolo has no utility family
- **THEN** the namespace remains available as effective-theme metadata without making utility syntax or CSS output available

### Requirement: BlueArt semantic theme entries use canonical Kolo custom properties
The system SHALL define BlueArt application-specific semantic colors under `--k-color-blueart-*` for page background, surfaces, author surface, borders, primary/action, accent, banner, body text, muted text, hashtag/link text, and light foreground values required by existing banner and action treatments.
The BlueArt overlay SHALL map these roles and any deliberate baseline overrides to the current exact visual values.
Theme-aware utilities MUST emit only `var(--k-...)` references and MUST NOT embed raw color or font values.
Kolo MUST NOT emit unprefixed custom properties.
Gradients, alpha composites, and other declarations without exact theme-backed utility parity MUST remain page-CSS exceptions.

#### Scenario: A theme-aware utility uses a BlueArt semantic color
- **WHEN** a supported utility resolves a BlueArt semantic color role
- **THEN** its generated declaration references the corresponding `var(--k-color-blueart-*)` property instead of a raw color value

#### Scenario: A non-equivalent composite declaration remains page owned
- **WHEN** a browse or art declaration uses a gradient or alpha composite without exact utility parity
- **THEN** the declaration remains in generated page CSS as a `kolo-exception`

### Requirement: CSS generation emits only requested effective-theme properties deterministically
The system SHALL provide a shared collector through which supported utility generators request exact effective-theme properties and associated animation keyframes.
The compiler SHALL emit one deterministic `:root` block before utility rules containing only requested `--k-*` properties, ordered by baseline order then contribution order and independent of request token order.
An overlay value SHALL replace the baseline value for the same property.
Associated keyframes SHALL be emitted outside `:root` exactly once when requested.
Registered entries and keyframes for unsupported utility families MUST NOT be emitted.
A compilation with no theme-aware utility requests MUST NOT emit a theme `:root` block.

#### Scenario: Multiple utilities request the same theme property
- **WHEN** supported utilities request the same effective-theme property during one compilation
- **THEN** the compiler emits the property once in the deterministic shared `:root` block before utility rules

#### Scenario: Token order changes
- **WHEN** the same theme-aware utilities are requested in a different token order
- **THEN** the emitted theme-property block retains the same deterministic property ordering

#### Scenario: No theme-aware utility is requested
- **WHEN** a stylesheet request contains no supported utility that requests theme metadata
- **THEN** the response contains no effective-theme `:root` block or theme keyframes

### Requirement: Theme consumption is family-owned and allow-listed
The system SHALL require each theme-aware utility family to expose only its documented, typed helpers and named baseline or semantic theme entries.
Typography SHALL own text-color helpers; background utilities SHALL own background-color helpers; and border utilities SHALL own border-color, radius, and width helpers.
A registered theme entry MUST NOT make utility syntax available unless its owning utility family implements and allow-lists that syntax.
Unknown theme names, malformed tokens, and unsupported property/value combinations MUST follow the existing deterministic diagnostic CSS path and MUST NOT fall back to raw CSS or a default theme value.

#### Scenario: An unknown semantic color token is requested
- **WHEN** a request contains a token that names an unsupported theme color
- **THEN** the compiler emits the existing deterministic diagnostic output and does not emit a fallback declaration

#### Scenario: A baseline value lacks an owning utility family
- **WHEN** a request attempts to use a baseline value whose utility family is not implemented
- **THEN** the compiler does not generate a utility rule solely because the baseline value is registered
