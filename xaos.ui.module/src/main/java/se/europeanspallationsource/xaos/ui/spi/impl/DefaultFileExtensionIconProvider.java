/*
 * Copyright 2018 European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.ui.spi.impl;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;
import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.spi.FileExtensionIconProvider;


/**
 * Provides default icons (i.e. {@link Node}s) for a given file extension.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
@ServiceProvider( service = FileExtensionIconProvider.class )
public class DefaultFileExtensionIconProvider implements FileExtensionIconProvider {

	private static final Map<String, Ikon> ICONS_MAP;

	/**
	 * static initializer.
	 */
	static {

		Ikon icon;
		Map<String, Ikon> map = new HashMap<>(100);

		//	Archive files...
		icon = FontAwesome.FILE_ARCHIVE_O;

		map.put("7x", icon);
		map.put("a##", icon);
		map.put("acb", icon);
		map.put("ace", icon);
		map.put("ar7", icon);
		map.put("arc", icon);
		map.put("ari", icon);
		map.put("arj", icon);
		map.put("ark", icon);
		map.put("arx", icon);
		map.put("b1", icon);
		map.put("ba", icon);
		map.put("bs2", icon);
		map.put("bsa", icon);
		map.put("bz2", icon);
		map.put("dmg", icon);
		map.put("dwc", icon);
		map.put("gz", icon);
		map.put("hbc", icon);
		map.put("hbe", icon);
		map.put("hpk", icon);
		map.put("hqx", icon);
		map.put("jar", icon);
		map.put("lif", icon);
		map.put("lzw", icon);
		map.put("lzx", icon);
		map.put("maff", icon);
		map.put("mar", icon);
		map.put("pka", icon);
		map.put("pkg", icon);
		map.put("pma", icon);
		map.put("ppk", icon);
		map.put("rar", icon);
		map.put("rpm", icon);
		map.put("sp", icon);
		map.put("tar", icon);
		map.put("taz", icon);
		map.put("tbz", icon);
		map.put("tgz", icon);
		map.put("tpz", icon);
		map.put("tz", icon);
		map.put("tzb", icon);
		map.put("uc2", icon);
		map.put("ucn", icon);
		map.put("war", icon);
		map.put("x", icon);
		map.put("yz", icon);
		map.put("z", icon);
		map.put("zip", icon);

		//	Audio/Music files...
		icon = FontAwesome.FILE_AUDIO_O;

		map.put("4md", icon);
		map.put("668", icon);
		map.put("669", icon);
		map.put("6cm", icon);
		map.put("8cm", icon);
		map.put("aac", icon);
		map.put("ad2", icon);
		map.put("ad3", icon);
		map.put("aif", icon);
		map.put("aifc", icon);
		map.put("aiff", icon);
		map.put("amr", icon);
		map.put("ams", icon);
		map.put("ape", icon);
		map.put("asf", icon);
		map.put("au", icon);
		map.put("aud", icon);
		map.put("audio", icon);
		map.put("cda", icon);
		map.put("cdm", icon);
		map.put("cfa", icon);
		map.put("enc", icon);
		map.put("flac", icon);
		map.put("m4a", icon);
		map.put("m4r", icon);
		map.put("mp2", icon);
		map.put("mp3", icon);
		map.put("mpa", icon);
		map.put("nxt", icon);
		map.put("ogg", icon);
		map.put("omg", icon);
		map.put("opus", icon);
		map.put("sf", icon);
		map.put("sfl", icon);
		map.put("smp", icon);
		map.put("snd", icon);
		map.put("son", icon);
		map.put("sound", icon);
		map.put("voc", icon);
		map.put("wav", icon);
		map.put("wave", icon);
		map.put("wma", icon);
		map.put("xa", icon);

		//	Code files...
		icon = FontAwesome.FILE_CODE_O;

		map.put("4th", icon);
		map.put("8", icon);
		map.put("a", icon);
		map.put("a80", icon);
		map.put("act", icon);
		map.put("ada", icon);
		map.put("adb", icon);
		map.put("ads", icon);
		map.put("ash", icon);
		map.put("asi", icon);
		map.put("asm", icon);
		map.put("bas", icon);
		map.put("bat", icon);
		map.put("bi", icon);
		map.put("c--", icon);
		map.put("c++", icon);
		map.put("c", icon);
		map.put("cas", icon);
		map.put("cbl", icon);
		map.put("cc", icon);
		map.put("cl", icon);
		map.put("coffee", icon);
		map.put("cs", icon);
		map.put("cxx", icon);
		map.put("d", icon);
		map.put("di", icon);
		map.put("e", icon);
		map.put("el", icon);
		map.put("erl", icon);
		map.put("esh", icon);
		map.put("f", icon);
		map.put("f4", icon);
		map.put("f77", icon);
		map.put("f90", icon);
		map.put("f95", icon);
		map.put("for", icon);
		map.put("gc1", icon);
		map.put("gc3", icon);
		map.put("go", icon);
		map.put("h--", icon);
		map.put("h++", icon);
		map.put("h", icon);
		map.put("hh", icon);
		map.put("hpp", icon);
		map.put("htm", icon);
		map.put("html", icon);
		map.put("hxx", icon);
		map.put("ipynb", icon);
		map.put("jav", icon);
		map.put("java", icon);
		map.put("js", icon);
		map.put("json", icon);
		map.put("kcl", icon);
		map.put("l", icon);
		map.put("lisp", icon);
		map.put("m", icon);
		map.put("mak", icon);
		map.put("mm", icon);
		map.put("p", icon);
		map.put("pas", icon);
		map.put("py", icon);
		map.put("r", icon);
		map.put("rb", icon);
		map.put("s", icon);
		map.put("sh", icon);
		map.put("sm", icon);
		map.put("st", icon);
		map.put("swift", icon);
		map.put("tcl", icon);
		map.put("xml", icon);

		//	Database files...
		icon = FontAwesome.DATABASE;

		map.put("3dt", icon);
		map.put("4db", icon);
		map.put("4dindy", icon);
		map.put("ab6", icon);
		map.put("ab8", icon);
		map.put("accda", icon);
		map.put("accdb", icon);
		map.put("accde", icon);
		map.put("accdt", icon);
		map.put("accdu", icon);
		map.put("accft", icon);
		map.put("ap", icon);
		map.put("bib", icon);
		map.put("cac", icon);
		map.put("cdb", icon);
		map.put("crp", icon);
		map.put("db", icon);
		map.put("db2", icon);
		map.put("db3", icon);
		map.put("dbf", icon);
		map.put("dbk", icon);
		map.put("dbx", icon);
		map.put("dtf", icon);
		map.put("fm", icon);
		map.put("fp3", icon);
		map.put("fp4", icon);
		map.put("fp5", icon);
		map.put("fp7", icon);
		map.put("fw", icon);
		map.put("fw2", icon);
		map.put("fw3", icon);
		map.put("idb", icon);
		map.put("ldb", icon);
		map.put("mat", icon);
		map.put("mdf", icon);
		map.put("ndb", icon);
		map.put("phf", icon);
		map.put("res", icon);
		map.put("rpd", icon);
		map.put("tdb", icon);

		//	Image/Picture files...
		icon = FontAwesome.FILE_IMAGE_O;

		map.put("555", icon);
		map.put("75", icon);
		map.put("a11", icon);
		map.put("acmb", icon);
		map.put("ais", icon);
		map.put("art", icon);
		map.put("b&w", icon);
		map.put("b_w", icon);
		map.put("b1n", icon);
		map.put("b8", icon);
		map.put("bga", icon);
		map.put("bif", icon);
		map.put("bmp", icon);
		map.put("cbm", icon);
		map.put("cco", icon);
		map.put("cdf", icon);
		map.put("ceg", icon);
		map.put("cgm", icon);
		map.put("cr2", icon);
		map.put("cut", icon);
		map.put("dcs", icon);
		map.put("ddb", icon);
		map.put("dem", icon);
		map.put("eps", icon);
		map.put("gif", icon);
		map.put("gry", icon);
		map.put("hdw", icon);
		map.put("iax", icon);
		map.put("ica", icon);
		map.put("icb", icon);
		map.put("ico", icon);
		map.put("idw", icon);
		map.put("j2c", icon);
		map.put("jff", icon);
		map.put("jfif", icon);
		map.put("jif", icon);
		map.put("jp2", icon);
		map.put("jpc", icon);
		map.put("jpeg", icon);
		map.put("jpg", icon);
		map.put("miff", icon);
		map.put("msp", icon);
		map.put("pcd", icon);
		map.put("pct", icon);
		map.put("pcx", icon);
		map.put("pda", icon);
		map.put("pdn", icon);
		map.put("pdd", icon);
		map.put("pic", icon);
		map.put("pict", icon);
		map.put("pix", icon);
		map.put("png", icon);
		map.put("pnt", icon);
		map.put("ppm", icon);
		map.put("ras", icon);
		map.put("raw", icon);
		map.put("rgb", icon);
		map.put("rif", icon);
		map.put("riff", icon);
		map.put("rl4", icon);
		map.put("rl8", icon);
		map.put("rla", icon);
		map.put("rlb", icon);
		map.put("rlc", icon);
		map.put("sg1", icon);
		map.put("sgi", icon);
		map.put("spi", icon);
		map.put("spiff", icon);
		map.put("sun", icon);
		map.put("tga", icon);
		map.put("tif", icon);
		map.put("tiff", icon);
		map.put("vda", icon);
		map.put("vgr", icon);
		map.put("vif", icon);
		map.put("viff", icon);
		map.put("vpg", icon);
		map.put("wim", icon);
		map.put("wpg", icon);
		map.put("xbm", icon);
		map.put("xcf", icon);
		map.put("xif", icon);
		map.put("xpm", icon);
		map.put("xwd", icon);

		//	Movie/Video files...
		icon = FontAwesome.FILE_MOVIE_O;

		map.put("byu", icon);
		map.put("f4a", icon);
		map.put("f4b", icon);
		map.put("f4p", icon);
		map.put("f4v", icon);
		map.put("fmv", icon);
		map.put("m2ts", icon);
		map.put("m4p", icon);
		map.put("m4v", icon);
		map.put("mov", icon);
		map.put("mp4", icon);
		map.put("mpeg", icon);
		map.put("mpg", icon);
		map.put("mpx", icon);
		map.put("mts", icon);
		map.put("qt", icon);
		map.put("qtvr", icon);
		map.put("sdc", icon);
		map.put("tmf", icon);
		map.put("trp", icon);
		map.put("ts", icon);
		map.put("ty", icon);
		map.put("vob", icon);
		map.put("vue", icon);
		map.put("wmv", icon);
		map.put("xmv", icon);

		//	PDF files...
		icon = FontAwesome.FILE_PDF_O;

		map.put("pdf", icon);

		//	Presentation/Powerpoint files...
		icon = FontAwesome.FILE_POWERPOINT_O;

		map.put("ch4", icon);
		map.put("key", icon);
		map.put("odp", icon);
		map.put("pcs", icon);
		map.put("pot", icon);
		map.put("pps", icon);
		map.put("ppt", icon);
		map.put("pptx", icon);
		map.put("psx", icon);
		map.put("shw", icon);
		map.put("sxi", icon);
		map.put("uop", icon);

		//	Spreadsheet files...
		icon = FontAwesome.FILE_EXCEL_O;

		map.put("123", icon);
		map.put("bwb", icon);
		map.put("cal", icon);
		map.put("col", icon);
		map.put("fm1", icon);
		map.put("fm3", icon);
		map.put("lcw", icon);
		map.put("lss", icon);
		map.put("mdl", icon);
		map.put("ods", icon);
		map.put("qbw", icon);
		map.put("slk", icon);
		map.put("sxc", icon);
		map.put("uos", icon);
		map.put("vc", icon);
		map.put("wk1", icon);
		map.put("wk3", icon);
		map.put("wk4", icon);
		map.put("wke", icon);
		map.put("wki", icon);
		map.put("wkq", icon);
		map.put("wks", icon);
		map.put("wkz", icon);
		map.put("wq1", icon);
		map.put("wr1", icon);
		map.put("xla", icon);
		map.put("xlb", icon);
		map.put("xlc", icon);
		map.put("xld", icon);
		map.put("xlk", icon);
		map.put("xll", icon);
		map.put("xlm", icon);
		map.put("xls", icon);
		map.put("xlsb", icon);
		map.put("xlsm", icon);
		map.put("xlsx", icon);
		map.put("xlt", icon);
		map.put("xlv", icon);
		map.put("xlw", icon);

		//	Text files...
		icon = FontAwesome.FILE_TEXT_O;

		map.put("1st", icon);
		map.put("602", icon);
		map.put("asc", icon);
		map.put("ftxt", icon);
		map.put("hex", icon);
		map.put("inf", icon);
		map.put("log", icon);
		map.put("me", icon);
		map.put("readme", icon);
		map.put("text", icon);
		map.put("txt", icon);

		//	Word Processor files...
		icon = FontAwesome.FILE_WORD_O;

		map.put("chi", icon);
		map.put("doc", icon);
		map.put("docm", icon);
		map.put("docx", icon);
		map.put("dot", icon);
		map.put("lwp", icon);
		map.put("odm", icon);
		map.put("odt", icon);
		map.put("ott", icon);
		map.put("pages", icon);
		map.put("pm3", icon);
		map.put("pm4", icon);
		map.put("pm5", icon);
		map.put("pt3", icon);
		map.put("pt4", icon);
		map.put("pt5", icon);
		map.put("pwp", icon);
		map.put("sxw", icon);
		map.put("uot", icon);
		map.put("wkb", icon);
		map.put("wp", icon);
		map.put("wp5", icon);
		map.put("wpd", icon);
		map.put("wri", icon);
		map.put("xlr", icon);

		ICONS_MAP = Collections.unmodifiableMap(map);

	}

	@Override
	public Node iconFor( String extension, int size ) {

		if ( StringUtils.isNotBlank(extension) && size > 0 ) {

			Ikon icon = ICONS_MAP.get(extension.toLowerCase());

			if ( icon != null ) {
				return FontIcon.of(icon, size);
			}

		}

		return null;

	}

}
