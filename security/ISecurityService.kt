package dk.group11.coursesystem.security

import org.springframework.security.core.Authentication


interface ISecurityService {

    fun getId(): Long

    fun getToken(): String
}