# Architecture

## Overview
BlueArt is a Kotlin + Spring Boot web application for browsing Bluesky/ATProto content with a server-rendered UI using `kotlinx.html`.

## Runtime Shape
- Server: Spring Boot MVC controllers.
- Rendering: server-side HTML generation (`kotlinx.html`).
- Build: Gradle Kotlin DSL with a multi-module layout (`:app`, `:libs`, `:libs:kolo-styles`, and `:visual-tests`).
- Styling: Kotlin CSS DSL endpoints under `/css/generated/*.css` generate browse/art page stylesheets from Kotlin in `:app` `CssController`; Kolo utilities are served from `/css/generated/kolo.css` by the `:libs:kolo-styles` module using tokenized query params during migration. Render-side collection/link plumbing lives in `:libs:kolo-styles` (`renderKoloHtml`, `kolo { ... }`, `koloStylesheetLink()`), with typed spacing helpers in `kolostyles.dsl.spacing`, typed layout helpers (including display + offsets + box-sizing/overflow/position/z-index/object-fit) under `kolostyles.dsl.layout`, typed font helpers in `kolostyles.dsl.font`, and typed sizing helpers in `kolostyles.dsl.sizing` (width/height/min/max families on both `KoloScope` and `KoloVariantScope`). Shared responsive metadata supports `width >=` min variants `sm` through `2xl` and exclusive `width <` max variants `max-sm` through `max-2xl`; the DSL exposes matching `maxSm` through `max2xl` scopes. `KoloStylesAutoConfiguration` is registered through Spring Boot metadata and component-scans the Kolo package, so its existing hook `@Component`s, `KoloCssCompiler` `@Service`, and `KoloCssController` are available independently of the consumer application's component scan. It composes the pinned generic Tailwind v4.1.11 Kolo baseline with ordered consumer `ThemeContribution`s once at startup into immutable `EffectiveKoloTheme`; `:app` supplies the BlueArt semantic overlay. During compilation, generators request only needed effective properties through `ThemePropertyCollector`, and the compiler places one ordered `:root` block before utilities plus deduplicated requested keyframes. For migrated browse/art elements, spacing, layout, selected typography, and selected sizing ownership is in Kolo tokens rather than `CssController`; `CssController` keeps non-migrated declarations (including palette/gradient `kolo-exception`s until color utilities exist, the non-equivalent 640px, 700px, and 960px responsive exceptions, global box-sizing preflight, and browser-specific line-clamp patterns).

## Module Boundaries
- `:app`: executable Spring Boot web application module. Contains controllers, HTML renderers, routes, and runtime wiring.
- `:libs`: reusable-library group module reserved for future shared libraries (including styling modules such as `kolo-styles`).
- `:libs:kolo-styles`: reusable styling library module for co-located style infrastructure primitives. Provides baseline utility/parser/generator contracts, the Kolo CSS compiler, and the Spring MVC adapter/configuration that owns `/css/generated/kolo.css`.
- `:visual-tests`: dedicated black-box visual regression module using Playwright + JUnit against localhost dummy-mode flows in headless Chromium/Firefox.

## Theme-Backed Typography
`FontParserHook` builds its family and weight lookups from the immutable effective theme at startup. Registered `--k-font-*` names are consumable through `fontFamily(FontFamily("display"))` on base and variant scopes; standard reserved weight names, the `weight-*` namespace, and double-hyphen metadata suffixes are excluded. Registered `--k-font-weight-*` entries support `fontWeight(FontWeight("book"))`, including numeric names only when explicitly registered. Eligible family/weight names sharing a utility token fail parser initialization at startup. Existing named helpers remain unchanged.

Sizes retain their named utility allow-list but identify effective `--k-text-*` properties rather than literal values. The baseline defines all supported sizes followed immediately by all companion `--k-text-*--line-height` entries in matching size order. The generator derives the companion property from the size property rather than storing redundant token metadata. Generators request both dependencies and use typed `kotlinx.css` setters with `var(...)` references, so consumer overrides apply consistently. BlueArt adopts the baseline line-height for its existing art-page `textBase` headings.

## Package Layout (`:app`)
App-owned endpoint controllers live under `com.github.cc007.blueart.endpoints`, preserving their function-specific subdirectory names:

| Package | Contents |
|---|---|
| `endpoints/auth` | `LoginController` |
| `endpoints/browse` | `BrowseController` |
| `endpoints/content/art` | `ArtContentController` |
| `endpoints/dummy` | `DummyAtProtoAuthController`, `DummyAtProtoTimelineController` |
| `endpoints/error` | `ErrorController` |
| `endpoints/styling` | `CssController` |
| `auth` | `AtProtoAuthentication`, `AtProtoAuthenticationProvider`, `SecurityConfig` (non-controller auth wiring) |
| `components` | `Header`, `PostSummary`, `RichTextFacetRenderer` (shared HTML components) |
| `util` | `Result`, `User` |

## Main Components
- `LoginController` renders the existing login form, including the `pdsUrl` selector that can switch the app into dummy localhost mode.
- `BrowseController` (`endpoints/browse`) renders timeline browsing routes.
- `PostSummary` renders feed-card snippets and navigation links, including media-aware card behavior (text-only cards clamp overflow; cards with embeds hide body text and use thumbnail/gallery layouts).
- `ArtContentController` (`endpoints/content/art`) renders art detail pages and comments.
- `RichTextFacetRenderer` converts ATProto rich-text facet byte ranges into safe link/tag/mention HTML segments shared by browse and art detail rendering.

## Primary Routes
- `GET /browse` timeline cards.
- `GET /art/{cid}?uri=...` art detail page.
- `GET /login` and `POST /login` shared login flow with a `localhost` dummy PDS support (running on localhost:8080).
- `POST /xrpc/com.atproto.server.createSession` localhost dummy session endpoint.
- `GET /xrpc/app.bsky.feed.getTimeline` localhost dummy timeline endpoint.

## Data Flow (High Level)
1. Controller fetches Bluesky/ATProto content through `work.socialhub.kbsky` APIs.
2. Domain objects are mapped directly into HTML views.
3. Controllers render with `renderKoloHtml { ... }`; `kolo { ... }` calls record tokens in a request-local collector during element rendering.
4. The renderer canonicalizes tokens and finalizes the `kolo.css` href (`/css/generated/kolo.css?version=...&kolo=...`) after HTML generation (placeholder replacement fallback).
5. Controllers link page stylesheet endpoints plus `kolo.css`; utility-covered declarations are removed incrementally from page CSS as migration progresses, with remaining spacing exceptions documented inline in `CssController`.

## Current Gaps
- `/art/{cid}` fallback lookup is best-effort when only CID is provided.
- Comment rendering is flattened instead of a fully nested tree.
