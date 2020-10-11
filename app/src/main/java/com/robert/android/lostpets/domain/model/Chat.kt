package com.robert.android.lostpets.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Chat(var id: Long?, val code: String, val fromUser: User, val toUser: User,
                val lastMessage: Message?, val unreadMessages: Long)
    : Parcelable {

    constructor(parcel: Parcel)
            : this(
            parcel.readValue(Long::class.java.classLoader) as Long?,
            parcel.readString()!!,
            parcel.readParcelable<User>(User::class.java.classLoader)!!,
            parcel.readParcelable<User>(User::class.java.classLoader)!!,
            parcel.readValue(Message::class.java.classLoader) as Message?,
            parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(code)
        parcel.writeParcelable(fromUser, flags)
        parcel.writeParcelable(toUser, flags)
        parcel.writeValue(lastMessage)
        parcel.writeLong(unreadMessages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chat> {

        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }
}
