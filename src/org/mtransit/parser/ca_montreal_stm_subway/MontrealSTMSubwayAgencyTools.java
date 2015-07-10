package org.mtransit.parser.ca_montreal_stm_subway;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.mt.data.MTrip;

// http://www.stm.info/en/about/developers
// http://www.stm.info/sites/default/files/gtfs/gtfs_stm.zip
public class MontrealSTMSubwayAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-montreal-stm-subway-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new MontrealSTMSubwayAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("\nGenerating STM subway data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("\nGenerating STM subway data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_SUBWAY;
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public long getRouteId(GRoute gRoute) {
		return Integer.valueOf(gRoute.route_short_name); // use route short name instead of route ID
	}

	@Override
	public String getRouteShortName(GRoute gRoute) {
		return null; // no route short name
	}

	private static final String BLEU = "BLEU";
	private static final String BLEUE = "BLEUE";

	@Override
	public String getRouteLongName(GRoute gRoute) {
		if (BLEU.equals(gRoute.route_long_name)) {
			return BLEUE;
		}
		return super.getRouteLongName(gRoute);
	}

	@Override
	public String getStopCode(GStop gStop) {
		return null; // no stop code
	}

	private static final String AGENCY_COLOR = "009EE0";

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_GREEN = "008449";
	private static final String COLOR_ORANGE = "E77710";
	private static final String COLOR_YELLOW = "FFD900";
	private static final String COLOR_BLUE = null; // same as agency color (009EE0)

	@Override
	public String getRouteColor(GRoute gRoute) {
		long routeId = getRouteId(gRoute);
		if (routeId == 1l) {
			return COLOR_GREEN;
		} else if (routeId == 2l) {
			return COLOR_ORANGE;
		} else if (routeId == 4l) {
			return COLOR_YELLOW;
		} else if (routeId == 5l) {
			return COLOR_BLUE;
		}
		System.out.println(String.format("Unexpected route '%s'", gRoute));
		System.exit(-1);
		return null;
	}

	private static final String ANGRIGNON = "angrignon";
	private static final String HONORE_BEAUGRAND = "honoré-beaugrand";
	private static final String COTE_VERTU = "côte-vertu";
	private static final String MONTMORENCY = "montmorency";
	private static final String HENRI_BOURASSA = "henri-bourassa";
	private static final String BERRI_UQAM = "berri-uqam";
	private static final String BERRI_UQAM2 = "berri-uqàm";
	private static final String LONGUEUIL_UNIVERSITE = "longueuil-université";
	private static final String LONGUEUIL_UNIVERSITE2 = "longueuil–université";
	private static final String SAINT_MICHEL = "saint-michel";
	private static final String SNOWDON = "snowdon";

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		String tripHeadsignLC = gTrip.trip_headsign.toLowerCase(Locale.ENGLISH);
		int directionId = -1;
		if (mRoute.id == 1l) {
			if (tripHeadsignLC.contains(ANGRIGNON)) { // green
				directionId = 0;
			} else if (tripHeadsignLC.contains(HONORE_BEAUGRAND)) { // green
				directionId = 1;
			}
		} else if (mRoute.id == 2l) {
			if (tripHeadsignLC.contains(COTE_VERTU)) { // orange
				directionId = 0;
			} else if (tripHeadsignLC.contains(MONTMORENCY) || tripHeadsignLC.contains(HENRI_BOURASSA)) { // orange
				directionId = 1;
			}
		} else if (mRoute.id == 4l) {
			if (tripHeadsignLC.contains(BERRI_UQAM) || tripHeadsignLC.contains(BERRI_UQAM2)) { // yellow
				directionId = 0;
			} else if (tripHeadsignLC.contains(LONGUEUIL_UNIVERSITE) || tripHeadsignLC.contains(LONGUEUIL_UNIVERSITE2)) { // yellow
				directionId = 1;
			}
		} else if (mRoute.id == 5l) {
			if (tripHeadsignLC.contains(SAINT_MICHEL)) { // blue
				directionId = 0;
			} else if (tripHeadsignLC.contains(SNOWDON)) { // blue
				directionId = 1;
			}
		}
		if (directionId < 0) {
			System.out.println("Unexpected trip: " + gTrip);
			System.exit(-1);
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.trip_headsign), directionId);
	}

	private static final Pattern UQAM = Pattern.compile("(uq[a|à]m)", Pattern.CASE_INSENSITIVE);
	private static final String UQAM_REPLACEMENT = "UQÀM";

	private static final Pattern UDEM = Pattern.compile("(universit[é|e](\\-| )de(\\-| )montr[é|e]al)", Pattern.CASE_INSENSITIVE);
	private static final String UDEM_REPLACEMENT = "UdeM";

	private static final Pattern U_DE_S = Pattern.compile("(universit[e|é](\\-| )de(\\-| )sherbrooke)", Pattern.CASE_INSENSITIVE);
	private static final String U_DE_S_REPLACEMENT = "UdeS";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = tripHeadsign.toLowerCase(Locale.ENGLISH);
		tripHeadsign = UQAM.matcher(tripHeadsign).replaceAll(UQAM_REPLACEMENT);
		tripHeadsign = U_DE_S.matcher(tripHeadsign).replaceAll(U_DE_S_REPLACEMENT);
		tripHeadsign = STATION.matcher(tripHeadsign).replaceAll(CleanUtils.SPACE);
		tripHeadsign = CleanUtils.SAINT.matcher(tripHeadsign).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static List<String> MMHB = Arrays.asList(new String[] { MONTMORENCY, HENRI_BOURASSA });
	private static String MMHB_HV = "Montmorency / Henri-Bourassa";

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		String tripHeadsignLC = mTrip.getHeadsignValue().toLowerCase(Locale.ENGLISH);
		if (MMHB.contains(tripHeadsignLC) && MMHB.contains(mTripToMerge.getHeadsignValue().toLowerCase(Locale.ENGLISH))) {
			mTrip.setHeadsignString(MMHB_HV, mTrip.getHeadsignId());
			return true;
		}
		return super.mergeHeadsign(mTrip, mTripToMerge);
	}

	private static final Pattern STATION = Pattern.compile("(station)", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanStopName(String stopName) {
		stopName = UQAM.matcher(stopName).replaceAll(UQAM_REPLACEMENT);
		stopName = UDEM.matcher(stopName).replaceAll(UDEM_REPLACEMENT);
		stopName = U_DE_S.matcher(stopName).replaceAll(U_DE_S_REPLACEMENT);
		stopName = STATION.matcher(stopName).replaceAll(CleanUtils.SPACE);
		stopName = CleanUtils.SAINT.matcher(stopName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		stopName = CleanUtils.cleanStreetTypesFRCA(stopName);
		return super.cleanStopName(stopName);
	}
}
