package com.gawkgawk.dao.user

import org.jetbrains.exposed.sql.Table

/**
 * Database table definition for users.
 */
object UserTable: Table(name = "users"){
    val id = long(name = "user_id").autoIncrement()
    val name = varchar(name = "user_name", length = 250)
    val email = varchar(name = "user_email", length = 250)
    val bio = text(name = "user_bio").default(
        defaultValue = "Hey, what's up? Welcome to Social page!"
    )
    val password = varchar(name = "user_password", length = 100)
    val imageUrl = text(name = "image_url").nullable()
    val followersCount = integer(name = "followers_count").default(defaultValue = 0)
    val followingCount = integer(name = "following_count").default(defaultValue = 0)

    override val primaryKey: PrimaryKey?
        get() =PrimaryKey(id)

}


/**
 * Data class representing a user.
 * @property id The ID of the user.
 * @property name The name of the user.
 * @property bio The bio of the user.
 * @property imageUrl The URL of the user, if available.
 * @property password The password of the user.
 * @property followersCount
 * @property followingCount
 */
data class UserRow(
    val id: Long,
    val name: String,
    val bio: String,
    val imageUrl: String?,
    val password: String,
    val followersCount: Int,
    val followingCount: Int
)