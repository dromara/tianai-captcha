# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.
必须用中文回复我
## Project Overview

tianai-captcha (天爱验证码/TAC) is a Java-based behavioral CAPTCHA library supporting multiple verification types:
- **SLIDER**: Slide-to-fit puzzle captcha
- **ROTATE**: Rotation verification captcha
- **CONCAT**: Slide-to-restore captcha
- **WORD_IMAGE_CLICK**: Text-click verification captcha

The project is a multi-module Maven project with Java 8 compatibility.

## Build Commands

```bash
# Build the entire project (skips tests by default)
mvn clean install

# Build without skipping tests
mvn clean install -DskipTests=false

# Build specific module
cd tianai-captcha
mvn clean install

# Package for deployment
mvn clean package

# Generate javadoc
mvn javadoc:javadoc
```

## Testing

```bash
# Run all tests
mvn test

# Run tests for specific module
cd tianai-captcha
mvn test

# Run a specific test class
mvn test -Dtest=TACBuilderTest

# Run a specific test method
mvn test -Dtest=TACBuilderTest#testMethod
```

## Project Structure

### Modules

1. **tianai-captcha** (core module)
   - Core captcha generation and validation logic
   - Generator implementations for each captcha type
   - Resource management system
   - Cache and storage abstractions

2. **tianai-captcha-springboot-starter**
   - Spring Boot auto-configuration
   - Redis integration support
   - Configuration properties binding

3. **tianai-captcha-solon-plugin**
   - Solon framework integration

4. **tianai-captcha-web-sdk**
   - Frontend JavaScript SDK

## Core Architecture

### Key Components

**ImageCaptchaApplication** (cloud.tianai.captcha.application.ImageCaptchaApplication)
- Main entry point for captcha operations
- Handles captcha generation via `generateCaptcha(type)`
- Handles validation via `matching(id, matchParam)`
- Manages lifecycle of generators, validators, and cache

**TACBuilder** (cloud.tianai.captcha.application.TACBuilder)
- Builder pattern for constructing ImageCaptchaApplication
- Fluent API for configuration
- Key methods:
  - `addDefaultTemplate()`: Load built-in templates
  - `addResource(type, resource)`: Add background images
  - `addTemplate(type, resourceMap)`: Add custom templates
  - `addFont(resource)`: Add fonts for WORD_IMAGE_CLICK
  - `cached(size, waitTime, period, expireTime)`: Enable pre-generation cache
  - `setCacheStore(store)`: Set cache implementation (local or distributed)
  - `setResourceStore(store)`: Set resource storage
  - `setTransform(transform)`: Set image transformation (default: Base64)

### Generator Layer

**MultiImageCaptchaGenerator** (cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator)
- Delegates to specific generators based on captcha type
- Manages resource loading and image transformation

**Specific Generators**:
- `StandardSliderImageCaptchaGenerator`: Generates slider puzzles
- `StandardRotateImageCaptchaGenerator`: Generates rotation captchas
- `StandardConcatImageCaptchaGenerator`: Generates slide-to-restore captchas
- `StandardWordClickImageCaptchaGenerator`: Generates text-click captchas

**CacheImageCaptchaGenerator** (cloud.tianai.captcha.generator.impl.CacheImageCaptchaGenerator)
- Wrapper that pre-generates captchas for improved performance
- Configurable cache size and refresh intervals

### Validator Layer

**ImageCaptchaValidator** (cloud.tianai.captcha.validator.ImageCaptchaValidator)
- Interface for captcha validation logic
- Default implementation: `SimpleImageCaptchaValidator`

**BasicCaptchaTrackValidator** (cloud.tianai.captcha.validator.impl.BasicCaptchaTrackValidator)
- Validates mouse/touch track data
- Checks for suspicious behavior patterns

### Resource Management

**ResourceStore** (cloud.tianai.captcha.resource.ResourceStore)
- Abstraction for storing captcha resources (images, templates)
- Implementations:
  - `LocalMemoryResourceStore`: In-memory storage
  - Custom implementations for distributed scenarios

**ImageCaptchaResourceManager** (cloud.tianai.captcha.resource.ImageCaptchaResourceManager)
- Manages resource loading and retrieval
- Default implementation: `DefaultImageCaptchaResourceManager`

**Resource** (cloud.tianai.captcha.resource.common.model.dto.Resource)
- Represents a resource with type and location
- Types: "classpath", "file", "url"

### Cache Layer

**CacheStore** (cloud.tianai.captcha.cache.CacheStore)
- Abstraction for caching captcha data
- Implementations:
  - `LocalCacheStore`: Local in-memory cache
  - Redis-based implementation in Spring Boot starter

### Image Transformation

**ImageTransform** (cloud.tianai.captcha.generator.ImageTransform)
- Converts generated images to desired format
- Default: `Base64ImageTransform` (JPG for background, PNG for template)

## Configuration

### Non-Spring Projects

Use TACBuilder to configure:

```java
ImageCaptchaApplication app = TACBuilder.builder()
    .addDefaultTemplate()
    .addResource("SLIDER", new Resource("classpath", "path/to/image.jpg"))
    .cached(20, 5000, 2000, 120000L)
    .build();
```

### Spring Boot Projects

Configure via application.yml:

```yaml
captcha:
  prefix: captcha                    # Cache key prefix
  expire:
    default: 120000                  # Default expiration (ms)
    WORD_IMAGE_CLICK: 180000        # Type-specific expiration
  init-default-resource: false       # Load built-in resources
  local-cache-enabled: true          # Enable pre-generation cache
  local-cache-size: 20              # Cache size
  local-cache-wait-time: 5000       # Wait time on cache miss (ms)
  local-cache-period: 2000          # Cache refresh interval (ms)
  font-path:                        # Font files for WORD_IMAGE_CLICK
    - classpath:font/simhei.ttf
```

## Important Constants

**CaptchaTypeConstant** (cloud.tianai.captcha.common.constant.CaptchaTypeConstant)
- `SLIDER`: Slide-to-fit captcha
- `ROTATE`: Rotation captcha
- `CONCAT`: Slide-to-restore captcha
- `WORD_IMAGE_CLICK`: Text-click captcha

**ParamKeyEnum** (cloud.tianai.captcha.generator.common.model.dto.ParamKeyEnum)
- `TOLERANT`: Configurable tolerance value for validation (added in recent commit)

## Key Data Flow

### Generation Flow
1. Client calls `ImageCaptchaApplication.generateCaptcha(type)`
2. `MultiImageCaptchaGenerator` selects appropriate generator
3. Generator loads resources via `ImageCaptchaResourceManager`
4. Generator creates captcha with random parameters
5. `ImageTransform` converts images to Base64 (or custom format)
6. Captcha data cached in `CacheStore` with unique ID
7. Returns `ImageCaptchaVO` with ID and image data

### Validation Flow
1. Client submits ID and track data via `matching(id, matchParam)`
2. Application retrieves cached captcha data by ID
3. `ImageCaptchaValidator` validates track against expected answer
4. `BasicCaptchaTrackValidator` checks track behavior patterns
5. Returns `ApiResponse` with success/failure status

## Extension Points

- **Custom Generators**: Implement `ImageCaptchaGenerator` interface
- **Custom Validators**: Implement `ImageCaptchaValidator` interface
- **Custom Cache**: Implement `CacheStore` (e.g., Redis, Memcached)
- **Custom Resources**: Implement `ResourceStore` for centralized resource management
- **Custom Transform**: Implement `ImageTransform` for different image formats
- **Interceptors**: Implement `CaptchaInterceptor` for pre/post processing

## Recent Changes

- Added support for Spring Boot 4 (commit: db0603a, 7c8730f)
- Fixed resource leak issues (commits: 16e517c, 29279e8)
- Added configurable tolerance value via `ParamKeyEnum.TOLERANT` (commit: a4f8a99)

## Documentation

- Online Demo: http://captcha.tianai.cloud
- Online Documentation: http://doc.captcha.tianai.cloud
- License: MulanPSL-2.0
