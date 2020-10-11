package com._2horizon.cva.dialogflow.manager.auth

import io.micronaut.security.authentication.UserDetails

/**
 * Created by Frank Lieber (liefra) on 2020-09-13.
 */
class EmailUserDetails(
    name: String,
    roles: Collection<String>,
    val email: String,
) : UserDetails(name, roles)
