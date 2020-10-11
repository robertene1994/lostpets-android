package com.robert.android.lostpets.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.robert.android.lostpets.domain.model.types.AdStatus
import com.robert.android.lostpets.domain.model.types.PetStatus
import java.util.Date

data class Ad(
    val id: Long?,
    val code: String?,
    val date: Date,
    val adStatus: AdStatus,
    val petStatus: PetStatus,
    val reward: Double,
    val lastSpottedCoords: LatLng,
    val pet: Pet,
    val observations: String,
    val photo: String?,
    val user: User
) :
    Parcelable {

    constructor(parcel: Parcel) :
            this(parcel.readLong(), parcel.readString(), Date(parcel.readLong()),
            AdStatus.values()[parcel.readInt()], PetStatus.values()[parcel.readInt()],
            parcel.readDouble(), parcel.readParcelable<LatLng>(LatLng::class.java.classLoader)!!,
            parcel.readParcelable<Pet>(Pet::class.java.classLoader)!!, parcel.readString()!!,
            parcel.readString(), parcel.readParcelable<User>(User::class.java.classLoader)!!)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ad) return false

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code?.hashCode() ?: 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id!!)
        parcel.writeString(code)
        parcel.writeLong(date.time)
        parcel.writeInt(adStatus.ordinal)
        parcel.writeInt(petStatus.ordinal)
        parcel.writeDouble(reward)
        parcel.writeParcelable(lastSpottedCoords, flags)
        parcel.writeParcelable(pet, flags)
        parcel.writeString(observations)
        parcel.writeString(photo)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ad> {

        override fun createFromParcel(parcel: Parcel): Ad {
            return Ad(parcel)
        }

        override fun newArray(size: Int): Array<Ad?> {
            return arrayOfNulls(size)
        }
    }
}
