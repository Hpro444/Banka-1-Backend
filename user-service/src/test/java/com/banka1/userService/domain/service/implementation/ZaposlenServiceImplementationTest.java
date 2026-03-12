package com.banka1.userService.domain.service.implementation;

import com.banka1.userService.configuration.AppProperties;
import com.banka1.userService.domain.Zaposlen;
import com.banka1.userService.domain.enums.Permission;
import com.banka1.userService.domain.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ZaposlenServiceImplementationTest {

    private ZaposlenServiceImplementation service;

    @BeforeEach
    void setUp() {
        AppProperties props = new AppProperties();
        props.setPermissions(Map.of(
                Role.BASIC, List.of(Permission.BANKING_BASIC, Permission.CLIENT_MANAGE),
                Role.AGENT, List.of(Permission.SECURITIES_TRADE_LIMITED),
                Role.SUPERVISOR, List.of(Permission.SECURITIES_TRADE_UNLIMITED, Permission.TRADE_UNLIMITED, Permission.OTC_TRADE, Permission.FUND_AGENT_MANAGE),
                Role.ADMIN, List.of(Permission.EMPLOYEE_MANAGE_ALL)
        ));
        service = new ZaposlenServiceImplementation(props);
    }

    @Test
    void setovanjePermisijaAssignsCorrectPermissionsForBasicRole() {
        Zaposlen zaposlen = new Zaposlen();
        zaposlen.setRole(Role.BASIC);

        service.setovanjePermisija(zaposlen);

        assertThat(zaposlen.getPermissionSet())
                .containsExactlyInAnyOrder(Permission.BANKING_BASIC, Permission.CLIENT_MANAGE);
    }

    @Test
    void setovanjePermisijaAssignsCorrectPermissionsForAdminRole() {
        Zaposlen zaposlen = new Zaposlen();
        zaposlen.setRole(Role.ADMIN);

        service.setovanjePermisija(zaposlen);

        assertThat(zaposlen.getPermissionSet())
                .containsExactlyInAnyOrder(
                        Permission.BANKING_BASIC, Permission.CLIENT_MANAGE,
                        Permission.SECURITIES_TRADE_LIMITED,
                        Permission.SECURITIES_TRADE_UNLIMITED, Permission.TRADE_UNLIMITED,
                        Permission.OTC_TRADE, Permission.FUND_AGENT_MANAGE,
                        Permission.EMPLOYEE_MANAGE_ALL
                );
    }
}
