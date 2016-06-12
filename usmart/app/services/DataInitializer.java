package services;

import java.util.Arrays;

import models.SecurityRole;

public class DataInitializer {
	
	public DataInitializer() {
        if (SecurityRole.find.findRowCount() == 0) {
            for (final String roleName : Arrays
                    .asList(controllers.HomeController.USER_ROLE)) {
                final SecurityRole role = new SecurityRole();
                role.roleName = roleName;
                role.save();
            }
        }
    }
}
