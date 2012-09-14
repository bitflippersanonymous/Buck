package com.bitflippersanonymous.buck.domain;

import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

// Needs to preserve through Create/Destroy
public class Dimension implements Parcelable {
	private int mWidth;  // FIXME: not sure if want these to be floats.
	private int mLength;
	
	@Override
	public int hashCode() {
		return mWidth ^ mLength;
	}
	
	@Override
	public boolean equals(Object o) {
		Dimension that = (Dimension)o;
		return mWidth == that.mWidth && mLength == that.mLength;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mWidth);
		dest.writeInt(mLength);
	}
	
	public static final Parcelable.Creator<Dimension> CREATOR = new Parcelable.Creator<Dimension>() {
		public Dimension createFromParcel(Parcel in) {
			return new Dimension(in);
		}

		public Dimension[] newArray(int size) {
			return new Dimension[size];
		}
	};

	private Dimension(Parcel in) {
		mWidth = in.readInt();
		mLength = in.readInt();
	}
			
	public Dimension(int width, int length) {
		mWidth = width;
		mLength = length;
	}
	public int getWidth() {
		return mWidth;
	}
	public void setWidth(int mWidth) {
		this.mWidth = mWidth;
	}
	public int getLength() {
		return mLength;
	}
	public void setLength(int mLength) {
		this.mLength = mLength;
	}
	
	private static Comparator<? super Dimension> sByLength = null;
	public static Comparator<? super Dimension> getByLength() {
		if ( sByLength == null )
			sByLength = new Comparator<Dimension>(){
			@Override
			public int compare(Dimension lhs, Dimension rhs) {
				return lhs.mLength - rhs.mLength;
			}};
		return sByLength;
	}

}