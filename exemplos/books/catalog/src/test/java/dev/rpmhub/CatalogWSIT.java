package dev.rpmhub;

import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
class CatalogWSIT extends CatalogWSTest {
    // Execute the same tests but in packaged mode.
    // Note: @TestSecurity only works with @QuarkusTest, so this IT class
    // is not exercised in the default build (skipITs=true).
}
