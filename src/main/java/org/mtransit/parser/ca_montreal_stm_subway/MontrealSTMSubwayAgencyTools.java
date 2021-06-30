package org.mtransit.parser.ca_montreal_stm_subway;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;

import java.util.Locale;
import java.util.regex.Pattern;

// https://www.stm.info/en/about/developers
// https://www.stm.info/sites/default/files/gtfs/gtfs_stm.zip
public class MontrealSTMSubwayAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new MontrealSTMSubwayAgencyTools().start(args);
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
	public long getRouteId(@NotNull GRoute gRoute) {
		return Long.parseLong(gRoute.getRouteShortName()); // use route short name instead of route ID
	}

	@Nullable
	@Override
	public String getRouteShortName(@NotNull GRoute gRoute) {
		return null; // no route short name
	}

	private static final String BLEU = "BLEU";
	private static final String BLEUE = "BLEUE";

	@NotNull
	@Override
	public String getRouteLongName(@NotNull GRoute gRoute) {
		if (BLEU.equals(gRoute.getRouteLongName())) {
			return BLEUE;
		}
		return super.getRouteLongName(gRoute);
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		return StringUtils.EMPTY; // no stop code
	}

	private static final String AGENCY_COLOR = "009EE0";

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_GREEN = "008449";
	private static final String COLOR_ORANGE = "E77710";
	private static final String COLOR_YELLOW = "FFD900";
	private static final String COLOR_BLUE = null; // same as agency color (009EE0)

	@Nullable
	@Override
	public String getRouteColor(@NotNull GRoute gRoute) {
		long routeId = getRouteId(gRoute);
		if (routeId == 1L) {
			return COLOR_GREEN;
		} else if (routeId == 2L) {
			return COLOR_ORANGE;
		} else if (routeId == 4L) {
			return COLOR_YELLOW;
		} else if (routeId == 5L) {
			return COLOR_BLUE;
		}
		throw new MTLog.Fatal("Unexpected route color '%s'", gRoute);
	}

	@Override
	public void setTripHeadsign(@NotNull MRoute mRoute, @NotNull MTrip mTrip, @NotNull GTrip gTrip, @NotNull GSpec gtfs) {
		mTrip.setHeadsignString(
				cleanTripHeadsign(gTrip.getTripHeadsignOrDefault()),
				gTrip.getDirectionIdOrDefault()
		);
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
		tripHeadsign = STATION_.matcher(tripHeadsign).replaceAll(CleanUtils.SPACE);
		tripHeadsign = CleanUtils.SAINT.matcher(tripHeadsign).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@Override
	public boolean mergeHeadsign(@NotNull MTrip mTrip, @NotNull MTrip mTripToMerge) {
		throw new MTLog.Fatal("%s: Using direction finder to merge %s and %s!", mTrip.getRouteId(), mTrip, mTripToMerge);
	}

	private static final Pattern STATION_ = Pattern.compile("(station )", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_DIGITS = Pattern.compile("( [\\d]+$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String stopName) {
		stopName = CleanUtils.toLowerCaseUpperCaseWords(Locale.FRENCH, stopName, "UQAM", "UQAM", "OACI", "IX");
		stopName = ENDS_WITH_DIGITS.matcher(stopName).replaceAll(StringUtils.EMPTY);
		stopName = UDEM.matcher(stopName).replaceAll(UDEM_REPLACEMENT);
		stopName = U_DE_S.matcher(stopName).replaceAll(U_DE_S_REPLACEMENT);
		stopName = STATION_.matcher(stopName).replaceAll(CleanUtils.SPACE);
		stopName = CleanUtils.SAINT.matcher(stopName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		stopName = CleanUtils.cleanStreetTypesFRCA(stopName);
		return super.cleanStopName(stopName);
	}
}
