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
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.ui.spi.MIMETypeIconProvider;


/**
 * Provides default icons (i.e. {@link Node}s) for a given MIME type.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
@ServiceProvider( service = MIMETypeIconProvider.class )
public class DefaultMIMETypeIconProvider implements MIMETypeIconProvider {

	private static final Map<String, Node> ICONS_MAP;

	/**
	 * static initializer.
	 */
	static {

		Node icon;
		Map<String, Node> map = new HashMap<>(100);

		//	Archive files...
		icon = FontIcon.of(FontAwesome.FILE_ARCHIVE_O);

		map.put("application/gzip", icon);
		map.put("application/zip", icon);
		map.put("application/zlib", icon);

		//	Audio/Music files...
		icon = FontIcon.of(FontAwesome.FILE_AUDIO_O);

		map.put("audio/1d-interleaved-parityfec", icon);
		map.put("audio/32kadpcm", icon);
		map.put("audio/3gpp", icon);
		map.put("audio/3gpp2", icon);
		map.put("audio/aac", icon);
		map.put("audio/ac3", icon);
		map.put("audio/AMR", icon);
		map.put("audio/AMR-WB", icon);
		map.put("audio/amr-wb+", icon);
		map.put("audio/aptx", icon);
		map.put("audio/asc", icon);
		map.put("audio/ATRAC-ADVANCED-LOSSLESS", icon);
		map.put("audio/ATRAC-X", icon);
		map.put("audio/ATRAC3", icon);
		map.put("audio/basic", icon);
		map.put("audio/BV16", icon);
		map.put("audio/BV32", icon);
		map.put("audio/clearmode", icon);
		map.put("audio/CN", icon);
		map.put("audio/DAT12", icon);
		map.put("audio/dls", icon);
		map.put("audio/dsr-es201108", icon);
		map.put("audio/dsr-es202050", icon);
		map.put("audio/dsr-es202211", icon);
		map.put("audio/dsr-es202212", icon);
		map.put("audio/DV", icon);
		map.put("audio/DVI4", icon);
		map.put("audio/eac3", icon);
		map.put("audio/encaprtp", icon);
		map.put("audio/EVRC", icon);
		map.put("audio/EVRC-QCP", icon);
		map.put("audio/EVRC0", icon);
		map.put("audio/EVRC1", icon);
		map.put("audio/EVRCB", icon);
		map.put("audio/EVRCB0", icon);
		map.put("audio/EVRCB1", icon);
		map.put("audio/EVRCNW", icon);
		map.put("audio/EVRCNW0", icon);
		map.put("audio/EVRCNW1", icon);
		map.put("audio/EVRCWB", icon);
		map.put("audio/EVRCWB0", icon);
		map.put("audio/EVRCWB1", icon);
		map.put("audio/EVS", icon);
		map.put("audio/example", icon);
		map.put("audio/fwdred", icon);
		map.put("audio/G711-0", icon);
		map.put("audio/G719", icon);
		map.put("audio/G7221", icon);
		map.put("audio/G722", icon);
		map.put("audio/G723", icon);
		map.put("audio/G726-16", icon);
		map.put("audio/G726-24", icon);
		map.put("audio/G726-32", icon);
		map.put("audio/G726-40", icon);
		map.put("audio/G728", icon);
		map.put("audio/G729", icon);
		map.put("audio/G7291", icon);
		map.put("audio/G729D", icon);
		map.put("audio/G729E", icon);
		map.put("audio/GSM", icon);
		map.put("audio/GSM-EFR", icon);
		map.put("audio/GSM-HR-08", icon);
		map.put("audio/iLBC", icon);
		map.put("audio/ip-mr_v2.5", icon);
		map.put("audio/L8", icon);
		map.put("audio/L16", icon);
		map.put("audio/L20", icon);
		map.put("audio/L24", icon);
		map.put("audio/LPC", icon);
		map.put("audio/MELP", icon);
		map.put("audio/MELP600", icon);
		map.put("audio/MELP1200", icon);
		map.put("audio/MELP2400", icon);
		map.put("audio/mobile-xmf", icon);
		map.put("audio/MPA", icon);
		map.put("audio/mp4", icon);
		map.put("audio/MP4A-LATM", icon);
		map.put("audio/mpa-robust", icon);
		map.put("audio/mpeg", icon);
		map.put("audio/mpeg4-generic", icon);
		map.put("audio/ogg", icon);
		map.put("audio/opus", icon);
		map.put("audio/parityfec", icon);
		map.put("audio/PCMA", icon);
		map.put("audio/PCMA-WB", icon);
		map.put("audio/PCMU", icon);
		map.put("audio/PCMU-WB", icon);
		map.put("audio/prs.sid", icon);
		map.put("audio/QCELP", icon);
		map.put("audio/raptorfec", icon);
		map.put("audio/RED", icon);
		map.put("audio/rtp-enc-aescm128", icon);
		map.put("audio/rtploopback", icon);
		map.put("audio/rtp-midi", icon);
		map.put("audio/rtx", icon);
		map.put("audio/SMV", icon);
		map.put("audio/SMV0", icon);
		map.put("audio/SMV-QCP", icon);
		map.put("audio/sp-midi", icon);
		map.put("audio/speex", icon);
		map.put("audio/t140c", icon);
		map.put("audio/t38", icon);
		map.put("audio/telephone-event", icon);
		map.put("audio/tone", icon);
		map.put("audio/UEMCLIP", icon);
		map.put("audio/ulpfec", icon);
		map.put("audio/usac", icon);
		map.put("audio/VDVI", icon);
		map.put("audio/VMR-WB", icon);
		map.put("audio/vnd.3gpp.iufp", icon);
		map.put("audio/vnd.4SB", icon);
		map.put("audio/vnd.audiokoz", icon);
		map.put("audio/vnd.CELP", icon);
		map.put("audio/vnd.cisco.nse", icon);
		map.put("audio/vnd.cmles.radio-events", icon);
		map.put("audio/vnd.cns.anp1", icon);
		map.put("audio/vnd.cns.inf1", icon);
		map.put("audio/vnd.dece.audio", icon);
		map.put("audio/vnd.digital-winds", icon);
		map.put("audio/vnd.dlna.adts", icon);
		map.put("audio/vnd.dolby.heaac.1", icon);
		map.put("audio/vnd.dolby.heaac.2", icon);
		map.put("audio/vnd.dolby.mlp", icon);
		map.put("audio/vnd.dolby.mps", icon);
		map.put("audio/vnd.dolby.pl2", icon);
		map.put("audio/vnd.dolby.pl2x", icon);
		map.put("audio/vnd.dolby.pl2z", icon);
		map.put("audio/vnd.dolby.pulse.1", icon);
		map.put("audio/vnd.dra", icon);
		map.put("audio/vnd.dts", icon);
		map.put("audio/vnd.dts.hd", icon);
		map.put("audio/vnd.dvb.file", icon);
		map.put("audio/vnd.everad.plj", icon);
		map.put("audio/vnd.hns.audio", icon);
		map.put("audio/vnd.lucent.voice", icon);
		map.put("audio/vnd.ms-playready.media.pya", icon);
		map.put("audio/vnd.nokia.mobile-xmf", icon);
		map.put("audio/vnd.nortel.vbk", icon);
		map.put("audio/vnd.nuera.ecelp4800", icon);
		map.put("audio/vnd.nuera.ecelp7470", icon);
		map.put("audio/vnd.nuera.ecelp9600", icon);
		map.put("audio/vnd.octel.sbc", icon);
		map.put("audio/vnd.presonus.multitrack", icon);
		map.put("audio/vnd.qcelp - DEPRECATED in favor of audio/qcelp", icon);
		map.put("audio/vnd.rhetorex.32kadpcm", icon);
		map.put("audio/vnd.rip", icon);
		map.put("audio/vnd.sealedmedia.softseal.mpeg", icon);
		map.put("audio/vnd.vmx.cvsd", icon);
		map.put("audio/vorbis", icon);
		map.put("audio/vorbis-config", icon);

		//	Code files...
		icon = FontIcon.of(FontAwesome.FILE_CODE_O);

		map.put("application/ecmascript", icon);
		map.put("application/javascript", icon);
		map.put("application/json", icon);
		map.put("application/json-patch+json", icon);
		map.put("application/json-seq", icon);
		map.put("application/xml", icon);
		map.put("application/xml-dtd", icon);
		map.put("application/xml-external-parsed-entity", icon);
		map.put("application/xml-patch+xml", icon);
		map.put("text/css", icon);
		map.put("text/html", icon);
		map.put("text/javascript", icon);
		map.put("text/xml", icon);
		map.put("text/xml-external-parsed-entity", icon);

		//	Database files...
		icon = FontIcon.of(FontAwesome.DATABASE);

		map.put("application/sql", icon);

		//	Image/Picture files...
		icon = FontIcon.of(FontAwesome.FILE_IMAGE_O);

		map.put("image/aces", icon);
		map.put("image/avci", icon);
		map.put("image/avcs", icon);
		map.put("image/bmp", icon);
		map.put("image/cgm", icon);
		map.put("image/dicom-rle", icon);
		map.put("image/emf", icon);
		map.put("image/example", icon);
		map.put("image/fits", icon);
		map.put("image/g3fax", icon);
		map.put("image/gif", icon);
		map.put("image/heic", icon);
		map.put("image/heic-sequence", icon);
		map.put("image/heif", icon);
		map.put("image/heif-sequence", icon);
		map.put("image/ief", icon);
		map.put("image/jls", icon);
		map.put("image/jp2", icon);
		map.put("image/jpeg", icon);
		map.put("image/jpm", icon);
		map.put("image/jpx", icon);
		map.put("image/ktx", icon);
		map.put("image/naplps", icon);
		map.put("image/png", icon);
		map.put("image/prs.btif", icon);
		map.put("image/prs.pti", icon);
		map.put("image/pwg-raster", icon);
		map.put("image/svg+xml", icon);
		map.put("image/t38", icon);
		map.put("image/tiff", icon);
		map.put("image/tiff-fx", icon);
		map.put("image/vnd.adobe.photoshop", icon);
		map.put("image/vnd.airzip.accelerator.azv", icon);
		map.put("image/vnd.cns.inf2", icon);
		map.put("image/vnd.dece.graphic", icon);
		map.put("image/vnd.djvu", icon);
		map.put("image/vnd.dwg", icon);
		map.put("image/vnd.dxf", icon);
		map.put("image/vnd.dvb.subtitle", icon);
		map.put("image/vnd.fastbidsheet", icon);
		map.put("image/vnd.fpx", icon);
		map.put("image/vnd.fst", icon);
		map.put("image/vnd.fujixerox.edmics-mmr", icon);
		map.put("image/vnd.fujixerox.edmics-rlc", icon);
		map.put("image/vnd.globalgraphics.pgb", icon);
		map.put("image/vnd.microsoft.icon", icon);
		map.put("image/vnd.mix", icon);
		map.put("image/vnd.ms-modi", icon);
		map.put("image/vnd.mozilla.apng", icon);
		map.put("image/vnd.net-fpx", icon);
		map.put("image/vnd.radiance", icon);
		map.put("image/vnd.sealed.png", icon);
		map.put("image/vnd.sealedmedia.softseal.gif", icon);
		map.put("image/vnd.sealedmedia.softseal.jpg", icon);
		map.put("image/vnd.svf", icon);
		map.put("image/vnd.tencent.tap", icon);
		map.put("image/vnd.valve.source.texture", icon);
		map.put("image/vnd.wap.wbmp", icon);
		map.put("image/vnd.xiff", icon);
		map.put("image/vnd.zbrush.pcx", icon);
		map.put("image/wmf", icon);
		map.put("image/x-emf", icon);
		map.put("image/x-wmf", icon);

		//	Movie/Video files...
		icon = FontIcon.of(FontAwesome.FILE_MOVIE_O);

		map.put("application/mp4", icon);
		map.put("application/mpeg4-generic", icon);
		map.put("application/mpeg4-iod", icon);
		map.put("application/mpeg4-iod-xmt", icon);
		map.put("application/ogg", icon);
		map.put("video/1d-interleaved-parityfec", icon);
		map.put("video/3gpp", icon);
		map.put("video/3gpp2", icon);
		map.put("video/3gpp-tt", icon);
		map.put("video/BMPEG", icon);
		map.put("video/BT656", icon);
		map.put("video/CelB", icon);
		map.put("video/DV", icon);
		map.put("video/encaprtp", icon);
		map.put("video/example", icon);
		map.put("video/H261", icon);
		map.put("video/H263", icon);
		map.put("video/H263-1998", icon);
		map.put("video/H263-2000", icon);
		map.put("video/H264", icon);
		map.put("video/H264-RCDO", icon);
		map.put("video/H264-SVC", icon);
		map.put("video/H265", icon);
		map.put("video/iso.segment", icon);
		map.put("video/JPEG", icon);
		map.put("video/jpeg2000", icon);
		map.put("video/mj2", icon);
		map.put("video/MP1S", icon);
		map.put("video/MP2P", icon);
		map.put("video/MP2T", icon);
		map.put("video/mp4", icon);
		map.put("video/MP4V-ES", icon);
		map.put("video/MPV", icon);
		map.put("video/mpeg", icon);
		map.put("video/mpeg4-generic", icon);
		map.put("video/nv", icon);
		map.put("video/ogg", icon);
		map.put("video/parityfec", icon);
		map.put("video/pointer", icon);
		map.put("video/quicktime", icon);
		map.put("video/raptorfec", icon);
		map.put("video/raw", icon);
		map.put("video/rtp-enc-aescm128", icon);
		map.put("video/rtploopback", icon);
		map.put("video/rtx", icon);
		map.put("video/smpte291", icon);
		map.put("video/SMPTE292M", icon);
		map.put("video/ulpfec", icon);
		map.put("video/vc1", icon);
		map.put("video/vnd.CCTV", icon);
		map.put("video/vnd.dece.hd", icon);
		map.put("video/vnd.dece.mobile", icon);
		map.put("video/vnd.dece.mp4", icon);
		map.put("video/vnd.dece.pd", icon);
		map.put("video/vnd.dece.sd", icon);
		map.put("video/vnd.dece.video", icon);
		map.put("video/vnd.directv.mpeg", icon);
		map.put("video/vnd.directv.mpeg-tts", icon);
		map.put("video/vnd.dlna.mpeg-tts", icon);
		map.put("video/vnd.dvb.file", icon);
		map.put("video/vnd.fvt", icon);
		map.put("video/vnd.hns.video", icon);
		map.put("video/vnd.iptvforum.1dparityfec-1010", icon);
		map.put("video/vnd.iptvforum.1dparityfec-2005", icon);
		map.put("video/vnd.iptvforum.2dparityfec-1010", icon);
		map.put("video/vnd.iptvforum.2dparityfec-2005", icon);
		map.put("video/vnd.iptvforum.ttsavc", icon);
		map.put("video/vnd.iptvforum.ttsmpeg2", icon);
		map.put("video/vnd.motorola.video", icon);
		map.put("video/vnd.motorola.videop", icon);
		map.put("video/vnd.mpegurl", icon);
		map.put("video/vnd.ms-playready.media.pyv", icon);
		map.put("video/vnd.nokia.interleaved-multimedia", icon);
		map.put("video/vnd.nokia.mp4vr", icon);
		map.put("video/vnd.nokia.videovoip", icon);
		map.put("video/vnd.objectvideo", icon);
		map.put("video/vnd.radgamettools.bink", icon);
		map.put("video/vnd.radgamettools.smacker", icon);
		map.put("video/vnd.sealed.mpeg1", icon);
		map.put("video/vnd.sealed.mpeg4", icon);
		map.put("video/vnd.sealed.swf", icon);
		map.put("video/vnd.sealedmedia.softseal.mov", icon);
		map.put("video/vnd.uvvu.mp4", icon);
		map.put("video/vnd.vivo", icon);
		map.put("video/VP8", icon);

		//	PDF files...
		icon = FontIcon.of(FontAwesome.FILE_PDF_O);

		map.put("application/pdf", icon);

		//	Presentation/Powerpoint files...
//		icon = FontIcon.of(FontAwesome.FILE_POWERPOINT_O);
//
//		map.put("xxx/xxx", icon);

		//	Spreadsheet files...
//		icon = FontIcon.of(FontAwesome.FILE_EXCEL_O);
//
//		map.put("xxx/xxx", icon);


		//	Text files...
		icon = FontIcon.of(FontAwesome.FILE_TEXT_O);

		map.put("application/rtf", icon);
		map.put("text/csv", icon);
		map.put("text/markdown", icon);
		map.put("text/plain", icon);
		map.put("text/richtext", icon);
		map.put("text/rtf", icon);
		map.put("text/strings", icon);

		//	Word Processor files...
		icon = FontIcon.of(FontAwesome.FILE_WORD_O);

		map.put("application/msword", icon);

		ICONS_MAP = Collections.unmodifiableMap(map);

	}

	@Override
	public Node iconFor( String mime ) {

		if ( StringUtils.isBlank(mime) ) {
			return null;
		}

		return ICONS_MAP.get(mime);

	}

}
