package com.robert.android.lostpets.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus

data class User(val id: Long?, val email: String, val password: String?, var role: Role,
                var status: UserStatus, val phone: String, val firstName: String,
                val lastName: String)
    : Parcelable {

    constructor(parcel: Parcel)
            : this(parcel.readLong(), parcel.readString()!!, parcel.readString(),
            Role.values()[parcel.readInt()], UserStatus.values()[parcel.readInt()],
            parcel.readString()!!, parcel.readString()!!, parcel.readString()!!)

    constructor(email: String, password: String, phone: String, firstName: String, lastName: String)
            : this(null, email, password, Role.USER, UserStatus.ENABLED, phone, firstName, lastName)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id!!)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeInt(role.ordinal)
        parcel.writeInt(status.ordinal)
        parcel.writeString(phone)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {

        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
