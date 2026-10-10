# Testing

## Scope
This project currently relies on Gradle test tasks and targeted checks around changed paths.

## Baseline Commands
```bash
./gradlew test
```

For module-scoped verification after the multi-module split:

```bash
./gradlew :app:test
```

Visual regression lane:

```bash
./gradlew visualTest
```

Baseline refresh (developer approval required):

```bash
./gradlew updateVisualBaselines -PvisualBaselineApprover=<developer-name>
```

## Change-Focused Guidance
- Prefer targeted tests for narrow changes.
- Run `./gradlew test` when edits are broad or touch shared code paths.
- Run `./gradlew visualTest` whenever AI diagnosis needs visual evidence for UI changes and before completing any change.
- Document any unverified areas in handoff notes when checks cannot run.

## Assertion Conventions
- Prefer KoTest matcher assertions (`shouldBe`, `shouldContain`, `shouldThrow`, null/collection/string matchers) over boolean-style `assertTrue`/`assertEquals` forms.
- Keep JUnit Platform execution unchanged (`kotlin("test-junit5")` baseline from shared `kotlin-jvm` convention plugin).
- For server-rendered HTML assertions, parse with Jsoup and assert with selector-aware checks instead of raw substring checks.
- Keep exact full-string assertions only when serialization format itself is the contract (for example canonical HTML/CSS output snapshots where byte-level ordering matters).

## Visual Baseline Policy
- Baseline updates require explicit in-editor developer approval.
- `-PvisualBaselineApprover` is mandatory for baseline updates.
- Approval strings that identify AI/Copilot are rejected by the visual baseline task.

## Deterministic GIF Rendering
The headless test context intercepts `.gif` requests (including query strings), decodes frame zero with ImageIO, and fulfills the original URL with a static PNG. Decoded frames are reused by URL for the test process to avoid repeated downloads and upstream rate limits. Fetch and decoding failures fail the scenario explicitly.

This substitution exists only in `VisualTestRuntime`; application assets and normal browser GIF playback are unchanged. CSS animation disabling does not control GIF playback. `gifFrameRemainsStable` checks loaded image pixels across a one-second interval in both Chromium and Firefox.

Responsive assertions cover both sides of the 960px browse and 700px art boundaries. Page-owned exceptions use selectors that outrank the later Kolo base utilities without altering desktop behavior.

## Visual Failure Triage
When `visualTest` fails, inspect expected/actual/diff artifacts under:

`visual-tests/build/reports/visual-regression/<browser>/<scenario>/`

Files:
- `expected.png` (committed baseline)
- `actual.png` (current run)
- `diff.png` (highlighted mismatch areas)

## Recent Coverage Additions
- `RichTextFacetRendererTest` validates UTF-8 byte-offset slicing plus defensive handling of malformed/overlapping link, tag, and mention facets.
- `PostSummaryTest` validates browse card rendering rules for BA-005 plus follow-record coverage (text-only card text rendering, split gallery wrappers for multi-image embeds, and `GraphFollow` activity rendering without unsupported placeholders).
- `LoginControllerTest` validates the login form exposes the `localhost` network option.
- `LoginNetworkSelectionTest` validates localhost network selection resolves to the running host and port while non-localhost selections stay on the existing path.
- `DummyLoginFlowTest` validates localhost form login, dummy browse follow-activity rendering, and dummy detail-page rendering through the live app.
- `DummyAtProtoAuthControllerTest` validates localhost dummy auth credentials and deterministic session payloads for `com.atproto.server.createSession`.
- `DummyAtProtoTimelineControllerTest` validates localhost dummy timeline paging/content shape (including deterministic cursor pagination and HTTPS direct image fixture URLs) plus bearer-token rejection for `app.bsky.feed.getTimeline`.
- `CssControllerTest` validates BA-003 step 2 stylesheet endpoints no longer use `@import` and preserve complete rule-header coverage from `static/css/browse.css` and `static/css/art.css` in generated Kotlin CSS DSL output.
- `KoloCssCompilerTest` validates BA-021 permissive token handling: preserve token order, annotate unsupported/unparsed tokens with CSS comments, and generate CSS through the `StyleParserHook` + `StyleGeneratorHook` pipeline.
- `KoloCssControllerTest` validates BA-021 `/css/generated/kolo.css` from `:libs:kolo-styles` always returns `200 text/css` and emits comment diagnostics for unsupported/unparsed tokens.
- `KoloHtmlRuntimeTest` validates BA-022 render-side plumbing: full HTML content assertions (not spot-checks) across all test cases, canonical token deduplication and variant-aware ordering, placeholder href finalization to `/css/generated/kolo.css`, per-element class scoping via the class-name mapper, multi-element overlap/deduplication, and no-op behaviour outside a kolo render context. Tests call `KoloScope.variant()` / `KoloScope.recordBase()` and `KoloVariantScope.variant()` / `KoloVariantScope.recordBase()` directly (no test-only adapter helpers).
- `DisplayUtilitiesTest` validates display utility parsing/generation coverage: full allow-list acceptance, unknown/malformed token diagnostics, pseudo/media variant rule generation, and mixed spacing+display compiler output order.
- `LayoutUtilitiesTest` validates non-display layout utility parsing/generation coverage (box-sizing, overflow, position, offsets, z-index, object-fit), including `z-auto`, representative positive-integer `z-<n>` values, pseudo/media variants, and mixed spacing+layout+display+font+sizing compiler output order.
- `FontUtilitiesTest` validates named sizes, effective-theme family/weight lookup, contributed fonts and custom weights (including explicitly registered numeric names), typography overrides, missing-property diagnostics, rejection of ambiguous family/weight names and metadata utilities, typed variable-only generation, baseline companion line-height retention and deduplication, variants, and mixed compiler ordering.
- `KoloTypographyStartupTest` proves ambiguous contributed family/weight names fail actual auto-configured Spring startup.
- Baseline companion line-heights intentionally change art-page `textBase` heading spacing. The art-detail snapshots in Chromium and Firefox require manual review; their baselines are left unchanged. Other visual cases remain unchanged.
- `SizingUtilitiesTest` validates sizing utility parsing/generation coverage across `w-*`, `h-*`, `min-w-*`, `max-w-*`, `min-h-*`, `max-h-*`, and `size-*`, including allow-list acceptance/rejection, pseudo/media variants, and `size-*` dual width+height emission.
- `KoloCssControllerTest` validates mixed spacing+display `/css/generated/kolo.css` generation when both hook families are wired, alongside unsupported/unparsed diagnostics behavior.
- `KoloCssControllerTest` also validates mixed spacing+display+font `/css/generated/kolo.css` generation when all hook families are wired.
- `KoloCssControllerTest` also validates mixed spacing+display+layout+font+sizing `/css/generated/kolo.css` generation when all utility hook families are wired.
- `CssControllerTest` validates migrated browse/art selectors no longer emit duplicate `display` declarations from generated page CSS once display ownership has moved into Kolo DSL render paths.
- `CssControllerTest` also validates migrated typography selectors omit duplicate `font-family`, `font-size`, and `font-weight` declarations while non-migrated typography exceptions remain explicit in generated page CSS.
- `CssControllerTest` also validates migrated selectors omit duplicated sizing declarations (`width`, `height`, `min/max-width`, `min/max-height`) while non-migrated arbitrary/css-var sizing exceptions remain explicit in generated page CSS.
- `CssControllerTest` also validates migrated selectors omit duplicated layout declarations (`overflow*`, `position`, offsets, `z-index`, `object-fit`) while non-migrated max-width/global/browser-specific layout exceptions remain explicit in generated page CSS.
- `KoloStylesApiTest` validates baseline `:libs:kolo-styles` placeholder API wiring for utility definitions and parser/generator hook contracts.
- `KoloStylesModuleWiringTest` validates the `:app` module can consume `:libs:kolo-styles` types without changing runtime behavior.
- `BlueArtApplicationTests` validates the Spring application context still starts with the Kolo compiler/configuration/controller owned by `:libs:kolo-styles`.
- `SpacingUtilitiesTest` validates BA-019 spacing utilities: `SpacingParserHook` accepts/rejects tokens correctly, `SpacingGeneratorHook` produces `k-`-prefixed CSS selectors with pseudo-class and media-query variant support, and `KoloCssCompiler` with spacing hook lists generates real CSS for spacing tokens.
- `KoloHtmlRuntimeTest` validates font DSL token/class emission and canonicalized `kolo.css` href output across base + variant scopes.
- `RichTextFacetRendererTest` validates mention/tag rich-text anchors emit semibold typography through co-located Kolo font utilities in render context.
- `PlaywrightVisualRegressionTest` in `:visual-tests` validates login, browse timeline card rendering, and art detail rendering in headless Chromium and Firefox against committed per-browser snapshots.
- `ResponsiveVariantsTest`, `KoloCssControllerTest`, and `KoloHtmlRuntimeTest` validate max-width parser acceptance, exclusive media output, unsupported maximum-variant diagnostics, and DSL token collection; `CssControllerTest` and Playwright boundary assertions retain the 640px, 700px, and 960px page-CSS exceptions.
- `KoloThemeTest` validates the self-contained pinned baseline against an independent test-only inventory, including unique properties, representative family values, legitimate `initial` settings, emission order, keyframe associations, contribution precedence, conflict diagnostics, validation, and immutable request-time metadata.
- `KoloCssCompilerTest`, `FontUtilitiesTest`, and `KoloCssControllerTest` validate on-demand theme root ordering/deduplication, keyframe-once emission, empty/unsupported requests, and canonical variable-only font-family generation.
- `BlueArtApplicationTests` validates startup composition of the BlueArt semantic overlay; `CssControllerTest` retains page-owned palette and composite declarations as explicit exceptions while Kolo color utilities do not yet exist.
- `BlueArtApplicationTests` also verifies that the auto-configured font parser sees contributed families and size/weight overrides. `KoloHtmlRuntimeTest` covers typed custom-family token delivery in base and variant scopes.
- `DummyLoginFlowTest` validates browse/art body font classes and coexistence of page styles with live Kolo CSS containing the exact BlueArt font overlay; `CssControllerTest` checks legacy font aliases are absent.

## Suggested Additions
- Lightweight integration tests for `GET /browse` and `GET /art/{cid}` rendering expectations.
