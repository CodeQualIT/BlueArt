## 1. Theme Contract and Baseline

- [ ] 1.1 Pin the Tailwind default theme-variable reference version and add a Kolo baseline fixture that inventories every supported namespace, property, and animation keyframe.
- [ ] 1.2 Implement typed, immutable theme-entry and keyframe metadata in a dedicated `:libs:kolo-styles` theme package, enforcing canonical `--k-*` property names and valid namespace/value shapes.
- [ ] 1.3 Translate the complete pinned Tailwind baseline into ordered `--k-*` metadata without introducing BlueArt-specific colors, fonts, or semantic roles.
- [ ] 1.4 Add baseline completeness tests that compare the registered namespaces, properties, and keyframe associations with the pinned fixture.

## 2. Effective Theme Composition

- [ ] 2.1 Define the ordered consumer-theme contribution contract, including contribution identity, precedence, property overrides, additions, and keyframe references.
- [ ] 2.2 Compose the generic baseline and discovered contributions once at application startup into an immutable effective-theme bean.
- [ ] 2.3 Validate contribution property names, namespaces, values, precedence, and keyframe references; fail startup with diagnostics for same-precedence conflicting values.
- [ ] 2.4 Add focused composition tests for baseline replacement, ordered additions, invalid contributions, conflict diagnostics, and immutable request-time access.

## 3. BlueArt Theme Overlay

- [ ] 3.1 Inventory `CssController` palette variables and raw font/color values, mapping each current visual role to a canonical BlueArt semantic property or an explicit page-CSS exception.
- [ ] 3.2 Finalize semantic names for the existing hashtag/link value and light foreground values, then document the selected BlueArt role-to-current-value mapping.
- [ ] 3.3 Implement the `:app` BlueArt contribution with current exact values for page background, surfaces, author surface, borders, primary/action, accent, banner, body text, muted text, hashtag/link text, and required light foreground roles.
- [ ] 3.4 Register the BlueArt overlay through the consumer contribution contract and add an application-context test proving that the effective theme includes its semantic entries and deliberate overrides.

## 4. On-Demand Theme CSS Generation

- [ ] 4.1 Add a per-compilation theme-property collector that lets generators request effective-theme properties and associated animation keyframes.
- [ ] 4.2 Integrate the collector with `KoloCssCompiler` so it emits one deterministic requested-property `:root` block before utility rules and deduplicated requested keyframes outside it.
- [ ] 4.3 Order emitted properties by baseline order followed by contribution order, independently of request-token order, and substitute overlay values for overridden baseline properties.
- [ ] 4.4 Preserve the existing canonical token ordering, request URL shape, responsive/pseudo variant semantics, and permissive diagnostics while adding theme collection.
- [ ] 4.5 Add compiler and endpoint tests for requested-property-only output, deterministic ordering, cross-family deduplication, overlay replacement, keyframe-once emission, no output for unsupported-only metadata, empty requests, and invalid theme references.

## 5. Theme-Aware Font Utilities

- [ ] 5.1 Refactor the font-family token metadata and generator to resolve its allow-listed family through the effective theme and emit `var(--k-font-*)` declarations.
- [ ] 5.2 Register font-family property requests with the shared collector without changing the existing typed font DSL tokens or family allow-list.
- [ ] 5.3 Add parser, generator, compiler, and endpoint coverage proving supported font families emit canonical variables and exact requested root properties without raw font-stack fallbacks.
- [ ] 5.4 Add render-runtime coverage proving existing font helpers retain canonical class and `kolo.css` URL generation while causing their theme property to be emitted.

## 6. Page CSS Migration Boundary

- [ ] 6.1 Update only page-CSS declarations with matching Kolo utility ownership to use canonical `--k-*` properties, adding the matching typed utility at each render site in the same change.
- [ ] 6.2 Retain page-local values for declarations that lack a matching utility request, and mark gradients, alpha composites, and other non-equivalent declarations as `kolo-exception`s.
- [ ] 6.3 Add temporary `CssController` aliases only for still-referenced legacy property names whose matching utility guarantees canonical-property emission; do not emit aliases from Kolo.
- [ ] 6.4 Remove each migrated duplicate declaration from `CssController` and remove temporary aliases once no generated browse or art stylesheet references them.
- [ ] 6.5 Add browse/art stylesheet and render tests for declaration ownership, alias lifetime, coexistence of page CSS with `kolo.css`, and font/palette visual parity.

## 7. Documentation and Verification

- [ ] 7.1 Update `docs/ARCHITECTURE.md` and `docs/DECISIONS.md` with the baseline/overlay boundary, startup composition, and on-demand theme-emission flow.
- [ ] 7.2 Update `docs/TESTING.md` and `docs/GLOSSARY.md` with theme test coverage, semantic theme terminology, and the bounded alias migration rule.
- [ ] 7.3 Run the targeted Kolo and app Gradle test suites, then run `./gradlew visualTest` to confirm browse and art visual parity.
- [ ] 7.4 Run `openspec validate --all` and resolve every validation error in the change artifacts.
