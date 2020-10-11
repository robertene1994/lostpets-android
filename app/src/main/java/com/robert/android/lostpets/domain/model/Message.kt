package com.robert.android.lostpets.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.robert.android.lostpets.domain.model.types.MessageStatus
import java.util.*

data class Message(val id: Long?, var code: String?, var content: String?, var date: Date,
                   var messageStatus: MessageStatus, var fromUser: User,
                   var toUser: User, var chat: Chat?)
    : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Message) return false

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code?.hashCode() ?: 0
    }

    constructor(parcel: Parcel)
            : this(
            parcel.readValue(Long::class.java.classLoader) as Long?,
            parcel.readString(),
            parcel.readString(), Date(parcel.readLong()),
            MessageStatus.values()[parcel.readInt()],
            parcel.readParcelable<User>(User::class.java.classLoader)!!,
            parcel.readParcelable<User>(User::class.java.classLoader)!!,
            parcel.readParcelable<Chat>(Chat::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(code)
        parcel.writeString(content)
        parcel.writeLong(date.time)
        parcel.writeInt(messageStatus.ordinal)
        parcel.writeParcelable(fromUser, flags)
        parcel.writeParcelable(toUser, flags)
        parcel.writeParcelable(chat, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {

        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}
