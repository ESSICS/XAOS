package se.europeanspallationsource.xaos.tools.lang;

@SuppressWarnings( "ClassWithoutLogger" )
class PackageOuterClass {

	@SuppressWarnings( "PackageVisibleField" )
	public int publicField = 111;
	@SuppressWarnings( "PackageVisibleField" )
	int packageField = 112;
	@SuppressWarnings( "PackageVisibleField" )
	protected int protectedField = 113;
	private int privateField = 114;

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
