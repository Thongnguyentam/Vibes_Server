package com.gawkgawk.dao.follows

import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object FollowsTable : Table(name = "follows"){
    val followerId = long(name = "follower_id") // the ID of the user who is following another user.
    val followingId = long(name = "following_id") // the ID of the user who is being followed
    val followDate = datetime(name = "follow_date").defaultExpression(defaultValue = CurrentDateTime)
    // the date and time when the follow action was created
}