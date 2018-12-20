package se.europeanspallationsource.xaos.tools.lang;

@SuppressWarnings( "ClassWithoutLogger" )
public class PublicOuterClass {

	@SuppressWarnings( "PublicField" )
	public int publicField = 101;
	@SuppressWarnings( "PackageVisibleField" )
	int packageField = 102;
	@SuppressWarnings( "ProtectedField" )
	protected int protectedField = 103;
	private int privateField = 104;

	public int getPublicField() {
		return publicField;
	}

	public void setPublicField( int publicField ) {
		this.publicField = publicField;
	}

	int getPackageField() {
		return packageField;
	}

	void setPackageField( int packageField ) {
		this.packageField = packageField;
	}

	protected int getProtectedField() {
		return protectedField;
	}

	protected void setProtectedField( int protectedField ) {
		this.protectedField = protectedField;
	}

	private int getPrivateField() {
		return privateField;
	}

	private void setPrivateField( int privateField ) {
		this.privateField = privateField;
	}

}
