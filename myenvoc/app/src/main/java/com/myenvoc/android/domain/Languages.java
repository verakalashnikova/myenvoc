package com.myenvoc.android.domain;

import com.myenvoc.commons.CommonUtils;

public enum Languages {
	
	SelectLang("","Select language"),
	Afrikaans("af","Afrikaans"),
	Albanian("sq","Albanian"),
	Arabic("ar","Arabic"),
	Belarusian("be","Belarusian"),
	Bulgarian("bg","Bulgarian"),
	Catalan("ca","Catalan"),
	Chinese("zh-CN","Chinese"),
	Croatian("hr","Croatian"),
	Czech("cs","Czech"),
	Danish("da","Danish"),
	Dutch("nl","Dutch"),
	English("en","English"),
	Estonian("et","Estonian"),
	Filipino("tl","Filipino"),
	Finnish("fi","Finnish"),
	French("fr","French"),
	Galician("gl","Galician"),
	German("de","German"),
	Greek("el","Greek"),
	Hebrew("iw","Hebrew"),
	Hindi("hi","Hindi"),
	Hungarian("hu","Hungarian"),
	Icelandic("is","Icelandic"),
	Indonesian("id","Indonesian"),
	Irish("ga","Irish"),
	Italian("it","Italian"),
	Japanese("ja","Japanese"),
	Korean("ko","Korean"),
	Latvian("lv","Latvian"),
	Lithuanian("lt","Lithuanian"),
	Macedonian("mk","Macedonian"),
	Malay("ms","Malay"),
	Maltese("mt","Maltese"),
	Norwegian("no","Norwegian"),
	Persian("fa","Persian"),
	Polish("pl","Polish"),
	Portuguese("pt","Portuguese"),
	Romanian("ro","Romanian"),
	Russian("ru","Russian"),
	Serbian("sr","Serbian"),
	Slovak("sk","Slovak"),
	Slovenian("sl","Slovenian"),
	Spanish("es","Spanish"),
	Swahili("sw","Swahili"),
	Swedish("sv","Swedish"),
	Thai("th","Thai"),
	Turkish("tr","Turkish"),
	Ukrainian("uk","Ukrainian"),
	Vietnamese("vi","Vietnamese"),
	Welsh("cy","Welsh"),
	Yiddish("yi","Yiddish");
		
	private String lang;
	private String label;
	private Languages(String lang, String label) {
		this.lang = lang;
		this.label = label;
	}
	
	@Override
	public String toString() {	
		return label;
	}

	public String getLang() {
		return lang;
	}

	public String getLabel() {
		return label;
	}

	public static Languages resolve(String nativeLanguage) {
		if (CommonUtils.isEmpty(nativeLanguage)) {
			return null;
		}
		for (Languages languages : values()) {
			if (languages.getLang().equals(nativeLanguage)) {
				return languages;
			}
		}
		return null;
	}
	
	
	

}
