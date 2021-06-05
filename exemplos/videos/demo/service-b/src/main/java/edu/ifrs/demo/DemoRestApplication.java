package edu.ifrs.demo;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/data")

@LoginConfig(authMethod = "MP-JWT", realmName = "jwt-jaspi")
@DeclareRoles({"protected"})

public class DemoRestApplication extends Application {
}
