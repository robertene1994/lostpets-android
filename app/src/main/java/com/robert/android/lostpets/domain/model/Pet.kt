package com.robert.android.lostpets.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.robert.android.lostpets.domain.model.types.Sex

data class Pet(val name: String, val type: String, val race: String,
               val sex: Sex, val colour: String, val microchipId: String)
    : Parcelable {

    constructor(parcel: Parcel)
            : this(parcel.readString()!!, parcel.readString()!!, parcel.readString()!!,
            Sex.values()[parcel.readInt()], parcel.readString()!!, parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeString(race)
        parcel.writeInt(sex.ordinal)
        parcel.writeString(colour)
        parcel.writeString(microchipId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pet> {

        override fun createFromParcel(parcel: Parcel): Pet {
            return Pet(parcel)
        }

        override fun newArray(size: Int): Array<Pet?> {
            return arrayOfNulls(size)
        }
    }
}
