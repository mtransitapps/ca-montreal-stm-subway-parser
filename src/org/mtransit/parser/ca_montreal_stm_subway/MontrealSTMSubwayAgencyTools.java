package org.mtransit.parser.ca_montreal_stm_subway;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MSpec;
import org.mtransit.parser.mt.data.MTrip;

// http:www.stm.info/en/about/developers
// http://www.stm.info/sites/default/files/gtfs/gtfs_stm.zip
public class MontrealSTMSubwayAgencyTools extends DefaultAgencyTools {

	public static final String ROUTE_TYPE_FILTER = "1"; // subway only
	public static final String SERVICE_ID_FILTER = "14S"; // TODO use calendar

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../ca-montreal-stm-subway-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new MontrealSTMSubwayAgencyTools().start(args);
	}

	@Override
	public void start(String[] args) {
		System.out.printf("Generating STM subway data...\n");
		long start = System.currentTimeMillis();
		super.start(args);
		System.out.printf("Generating STM subway data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		if (ROUTE_TYPE_FILTER != null && !gRoute.route_type.equals(ROUTE_TYPE_FILTER)) {
			return true;
		}
		return super.excludeRoute(gRoute);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (SERVICE_ID_FILTER != null && !gTrip.service_id.contains(SERVICE_ID_FILTER)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (SERVICE_ID_FILTER != null && !gCalendarDates.service_id.contains(SERVICE_ID_FILTER)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (SERVICE_ID_FILTER != null && !gCalendar.service_id.contains(SERVICE_ID_FILTER)) {
			return true;
		}
		return false;
	}

	@Override
	public int getRouteId(GRoute gRoute) {
		return Integer.valueOf(gRoute.route_short_name); // use route short name instead of route ID
	}

	@Override
	public String getRouteShortName(GRoute gRoute) {
		return null; // no route short name
	}

	@Override
	public String getStopCode(GStop gStop) {
		return null; // no stop code
	}

	private static final String COLOR_WHITE = "FFFFFF";

	@Override
	public String getRouteTextColor(GRoute gRoute) {
		return COLOR_WHITE; // better contrast
	}

	private static final String ANGRIGNON = "Angrignon";
	private static final String HONORE_BEAUGRAND = "Honoré-Beaugrand";
	private static final String COTE_VERTU = "Côte-Vertu";
	private static final String MONTMORENCY = "Montmorency";
	private static final String HENRI_BOURASSA = "Henri-Bourassa";
	private static final String BERRI_UQAM = "Berri-UQAM";
	private static final String LONGUEUIL_UNIVERSITE = "Longueuil-Université";
	private static final String SAINT_MICHEL = "Saint-Michel";
	private static final String SNOWDON = "Snowdon";

	@Override
	public void setTripHeadsign(MTrip mTrip, GTrip gTrip) {
		String stationName = cleanStopName(gTrip.trip_headsign);
		int directionId = -1;
		if (stationName.contains(ANGRIGNON)) { // green
			directionId = 0;
		} else if (stationName.contains(HONORE_BEAUGRAND)) { // green
			directionId = 1;
		} else if (stationName.contains(COTE_VERTU)) { // orange
			directionId = 0;
		} else if (stationName.contains(MONTMORENCY) || stationName.contains(HENRI_BOURASSA)) { // orange
			directionId = 1;
		} else if (stationName.contains(BERRI_UQAM)) { // yellow
			directionId = 0;
		} else if (stationName.contains(LONGUEUIL_UNIVERSITE)) { // yellow
			directionId = 1;
		} else if (stationName.contains(SAINT_MICHEL)) { // blue
			directionId = 0;
		} else if (stationName.contains(SNOWDON)) { // blue
			directionId = 1;
		} else {
			System.out.println("Unexpected station: " + stationName + " (headsign: " + gTrip.trip_headsign + ")");
			System.exit(-1);
		}
		mTrip.setHeadsignString(stationName, directionId);
	}

	private static List<String> MMHB = Arrays.asList(new String[] { MONTMORENCY, HENRI_BOURASSA });
	private static String MMHB_HV = "Montmorency / Henri-Bourassa";

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		if (MMHB.contains(mTrip.getHeadsignValue()) && MMHB.contains(mTripToMerge.getHeadsignValue())) {
			mTrip.setHeadsignString(MMHB_HV, mTrip.getHeadsignId());
			return true;
		}
		return super.mergeHeadsign(mTrip, mTripToMerge);
	}

	private static final Pattern STATION = Pattern.compile("(station)", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanStopName(String result) {
		result = STATION.matcher(result).replaceAll(MSpec.SPACE);
		return super.cleanStopName(result);
	}

}
