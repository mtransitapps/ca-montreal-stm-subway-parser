package org.mtransit.parser.ca_montreal_stm_subway;

import static org.mtransit.commons.CleanUtils.SPACE;
import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// https://www.stm.info/en/about/developers
public class MontrealSTMSubwayAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new MontrealSTMSubwayAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_FR_EN;
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "STM";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_SUBWAY;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return false; // route ID == default route short name
	}

	@NotNull
	@Override
	public String getRouteShortName(@NotNull GRoute gRoute) {
		return EMPTY; // no route short name
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return false; // route ID used to target Twitter news ...
	}

	@NotNull
	@Override
	public String cleanRouteShortName(@NotNull String routeShortName) {
		return super.cleanRouteShortName(routeShortName);
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		if ("BLEU".equals(routeLongName)) {
			return "BLEUE";
		}
		return super.cleanRouteLongName(routeLongName);
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		return EMPTY; // no stop code
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	@Nullable
	@Override
	public String fixColor(@Nullable String color) {
		if (color != null && !color.isEmpty()) {
			return color; // keep provided colors
		}
		return super.fixColor(color);
	}

	private static final Pattern UDEM = CleanUtils.cleanWords("universit[é|e][-| ]de[-| ]montr[é|e]al");
	private static final String UDEM_REPLACEMENT = CleanUtils.cleanWordsReplacement("UdeM");

	private static final Pattern U_DE_S = CleanUtils.cleanWords("universit[e|é][-| ]de[-| ]sherbrooke");
	private static final String U_DE_S_REPLACEMENT = CleanUtils.cleanWordsReplacement("UdeS");

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.toLowerCaseUpperCaseWords(Locale.FRENCH, tripHeadsign, "UQAM", "UQAM", "OACI", "IX");
		tripHeadsign = U_DE_S.matcher(tripHeadsign).replaceAll(U_DE_S_REPLACEMENT);
		tripHeadsign = STATION_.matcher(tripHeadsign).replaceAll(SPACE);
		tripHeadsign = CleanUtils.SAINT.matcher(tripHeadsign).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern STATION_ = Pattern.compile("(station )", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_DIGITS = Pattern.compile("( [\\d]+$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String stopName) {
		stopName = CleanUtils.toLowerCaseUpperCaseWords(Locale.FRENCH, stopName, "UQAM", "UQAM", "OACI", "IX");
		stopName = ENDS_WITH_DIGITS.matcher(stopName).replaceAll(EMPTY);
		stopName = UDEM.matcher(stopName).replaceAll(UDEM_REPLACEMENT);
		stopName = U_DE_S.matcher(stopName).replaceAll(U_DE_S_REPLACEMENT);
		stopName = STATION_.matcher(stopName).replaceAll(SPACE);
		stopName = CleanUtils.SAINT.matcher(stopName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		stopName = CleanUtils.cleanStreetTypesFRCA(stopName);
		return super.cleanStopName(stopName);
	}
}
