# Kolo Utility Architecture

## Provenance
- `BA-016` (`docs/ai-tasks/2026-05-06-BA-016-co-located-tailwind-like-styling-architecture.md`)
- `docs/DECISIONS.md` (D-007)

## Purpose
Define core Kolo utility-token architecture, canonicalization rules, and migration-era stylesheet coexistence.

## Requirements

### Requirement: Kolo utility tokens follow canonical architecture contracts
The system SHALL define explicit compiler token contracts for utility generation. Parser hooks SHALL parse raw utility strings into typed tokens, and generator hooks SHALL consume those typed tokens to emit CSS through the compiler pipeline. Supported utility families MUST include spacing, layout, font-family, font-size, font-weight, and sizing tokens in the same parser/generator hook architecture. The layout family MUST include display, box-sizing, overflow, position, inset/top/right/bottom/left offsets, z-index, and object-fit tokens. All utility families MUST use shared responsive variant metadata that defines the canonical variant name, direction (`min` or `max`), and CSS boundary for supported minimum-width and maximum-width variants.

#### Scenario: Tokens are prepared for stylesheet URL generation
- **WHEN** Kolo tokens are finalized for stylesheet delivery
- **THEN** they are canonicalized according to deterministic ordering and delimiter constraints

#### Scenario: Spacing utility token is parsed for generation
- **WHEN** a supported spacing token is parsed
- **THEN** a typed spacing compiler token is produced with raw token identity, utility metadata, and resolved spacing value

#### Scenario: Display utility token is parsed for generation
- **WHEN** a supported display token is parsed
- **THEN** a typed layout compiler token is produced with raw token identity and resolved display utility metadata

#### Scenario: Non-display layout utility token is parsed for generation
- **WHEN** a supported layout token for box-sizing, overflow, position, offset, z-index, or object-fit is parsed
- **THEN** a typed layout compiler token is produced with raw token identity and resolved layout utility metadata

#### Scenario: Font utility token is parsed for generation
- **WHEN** a supported font-family, font-size, or font-weight token is parsed
- **THEN** a typed font compiler token is produced with raw token identity and resolved font utility metadata

#### Scenario: Sizing utility token is parsed for generation
- **WHEN** a supported sizing token is parsed
- **THEN** a typed sizing compiler token is produced with raw token identity and resolved sizing utility metadata

#### Scenario: Maximum-width qualified utility token is parsed for generation
- **WHEN** a supported utility token is qualified by a supported `max-*` responsive variant
- **THEN** its typed compiler token includes shared maximum-width responsive metadata with the canonical variant name and exclusive CSS boundary

#### Scenario: Generator receives unsupported token type
- **WHEN** a generator hook receives a token type it does not support
- **THEN** it returns `false` and leaves emission to other hooks or compiler diagnostics

### Requirement: Kolo stylesheet delivery coexists with page CSS during migration
The system SHALL support side-by-side delivery of `kolo.css` and page-specific CSS during incremental utility migration.

#### Scenario: Utility migration is partial
- **WHEN** only a subset of declarations has moved to Kolo utilities
- **THEN** both utility and page stylesheet paths remain active without requiring full migration cutover
