# Architecture

## Overview
BlueArt is a Kotlin + Spring Boot web application for browsing Bluesky/ATProto content with a server-rendered UI using `kotlinx.html`.

## Runtime Shape
- Server: Spring Boot MVC controllers.
- Rendering: server-side HTML generation (`kotlinx.html`).
- Build: Gradle Kotlin DSL with a multi-module layout (`:app`, `:libs`, `:libs:kolo-styles`, and `:visual-tests`).
- Styling: Kotlin CSS DSL endpoints under `/css/generated/*.css` generate browse/art page stylesheets from Kotlin in `:app` `CssController`; Kolo utilities are served from `/css/generated/kolo.css` by the `:libs:kolo-styles` module using tokenized query params during migration. Render-side collection/link plumbing lives in `:libs:kolo-styles` (`renderKoloHtml`, `kolo { ... }`, `koloStylesheetLink()`), with typed spacing helpers in `kolostyles.dsl.spacing`, typed layout helpers (including display + offsets + box-sizing/overflow/position/z-index/object-fit) under `kolostyles.dsl.layout`, typed font helpers in `kolostyles.dsl.font`, and typed sizing helpers in `kolostyles.dsl.sizing` (width/height/min/max families on both `KoloScope` and `KoloVariantScope`). Shared responsive metadata supports min-width `sm` through `2xl` and exclusive max-width `max-sm` through `max-2xl`; the DSL exposes matching `maxSm` through `max2xl` scopes. Hook implementations (`SpacingParserHook`/`SpacingGeneratorHook`, `layout.display.DisplayParserHook`/`layout.display.DisplayGeneratorHook`, `LayoutParserHook`/`LayoutGeneratorHook`, `layout.offset.OffsetParserHook`/`OffsetGeneratorHook`, `FontParserHook`/`FontGeneratorHook`, and `SizingParserHook`/`SizingGeneratorHook`) are registered as Spring `@Component` beans, and `KoloCssCompiler` is a Spring `@Service` that consumes the injected `List<StyleParserHook>` + `List<StyleGeneratorHook>`. For migrated browse/art elements, spacing, layout, selected typography, and selected sizing ownership is in Kolo tokens rather than `CssController`; `CssController` keeps non-migrated declarations (including the non-equivalent 640px, 700px, and 960px responsive `kolo-exception`s, global box-sizing preflight, and browser-specific line-clamp patterns).

## Module Boundaries
- `:app`: executable Spring Boot web application module. Contains controllers, HTML renderers, routes, and runtime wiring.
- `:libs`: reusable-library group module reserved for future shared libraries (including styling modules such as `kolo-styles`).
- `:libs:kolo-styles`: reusable styling library module for co-located style infrastructure primitives. Provides baseline utility/parser/generator contracts, the Kolo CSS compiler, and the Spring MVC adapter/configuration that owns `/css/generated/kolo.css`.
- `:visual-tests`: dedicated black-box visual regression module using Playwright + JUnit against localhost dummy-mode flows in headless Chromium/Firefox.

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
