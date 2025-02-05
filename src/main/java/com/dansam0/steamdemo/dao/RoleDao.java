package com.dansam0.steamdemo.dao;

import com.dansam0.steamdemo.entity.Role;

public interface RoleDao {

    Role findRoleByName(String name);
}
