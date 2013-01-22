/*
 * Copyright (C) 2005-2013 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * Unless you have purchased a commercial license agreement from ManyDesigns srl,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * There are special exceptions to the terms and conditions of the GPL
 * as it is applied to this software. View the full text of the
 * exception in file OPEN-SOURCE-LICENSE.txt in the directory of this
 * software distribution.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307  USA
 *
 */

package com.manydesigns.portofino.pageactions.calendar;

import com.manydesigns.portofino.calendar.AbstractDay;
import com.manydesigns.portofino.calendar.AbstractMonthView;
import com.manydesigns.portofino.calendar.AbstractWeek;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.*;

/**
 * @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
 * @author Angelo Lupo          - angelo.lupo@manydesigns.com
 * @author Giampiero Granatella - giampiero.granatella@manydesigns.com
 * @author Alessio Stalla       - alessio.stalla@manydesigns.com
 */
public class MonthView extends AbstractMonthView<MonthView.MonthViewWeek> {
    public static final String copyright =
            "Copyright (c) 2005-2013, ManyDesigns srl";

    enum Search {
        BEFORE, DURING, AFTER
    }

    //--------------------------------------------------------------------------
    // Constructors and builder overrides
    //--------------------------------------------------------------------------

    public MonthView(DateTime referenceDateTime) {
        super(referenceDateTime);
    }

    public MonthView(DateTime referenceDateTime, int firstDayOfWeek) {
        super(referenceDateTime, firstDayOfWeek);
    }

    @Override
    protected MonthViewWeek[] createWeeksArray(int size) {
        return new MonthViewWeek[size];
    }

    @Override
    protected MonthViewWeek createWeek(DateMidnight weekStart, DateMidnight weekEnd) {
        return new MonthViewWeek(weekStart, weekEnd);
    }



    //--------------------------------------------------------------------------
    // Events
    //--------------------------------------------------------------------------

    public int addEvents(Collection<Event> events) {
        int counter = 0;
        for (Event event : events) {
            boolean result = addEvent(event);
            if (result) {
                counter++;
            }
        }
        logger.debug("Added {} events", counter);
        return counter;
    }

    public boolean addEvent(Event event) {
        Interval monthViewOverlap =
                monthViewInterval.overlap(event.getInterval());
        if (monthViewOverlap == null) {
            logger.debug("Event not overlapping with month view");
            return false;
        } else {
            logger.debug("Event overlapping with month view");
            boolean weekSanityCheck = false;
            for (MonthViewWeek week : weeks) {
                weekSanityCheck = week.addEvent(event) || weekSanityCheck;
            }
            if (!weekSanityCheck) {
                logger.warn("Sanity check failed: Event overlaps with month but not with month's weeks.");
            }
            return true;
        }
    }

    public void clearEvents() {
        logger.debug("Clearing events");
        for (MonthViewWeek week : weeks) {
            week.clearEvents();
        }
    }

    public void sortEvents() {
        logger.debug("Sorting events");
        for (MonthViewWeek week : weeks) {
            week.sortEvents();
        }
    }



    //--------------------------------------------------------------------------
    // Accessory classes
    //--------------------------------------------------------------------------

    public class MonthViewWeek extends AbstractWeek<MonthViewDay> {
        final List<EventWeek> eventWeekOverlaps;

        public MonthViewWeek(DateMidnight weekStart, DateMidnight weekEnd) {
            super(weekStart, weekEnd);
            eventWeekOverlaps = new ArrayList<EventWeek>();
        }

        @Override
        protected MonthViewDay[] createDaysArray(int size) {
            return new MonthViewDay[size];
        }

        @Override
        protected MonthViewDay createDay(DateMidnight dayStart, DateMidnight dayEnd) {
            return new MonthViewDay(dayStart, dayEnd);
        }

        public void clearEvents() {
            eventWeekOverlaps.clear();
        }

        public boolean addEvent(Event event) {
            Interval eventInterval = event.getInterval();
            Interval weekOverlap = weekInterval.overlap(eventInterval);
            if (weekOverlap == null) {
                logger.debug("Event not overlapping with week");
                return false;
            } else {
                logger.debug("Event overlapping with week.");
                logger.debug("Iterating on the days.");
                Integer startDay = null;
                Integer endDay = null;
                Search search = Search.BEFORE;
                for (int i = 0; i < 7; i++) {
                    MonthViewDay day = getDay(i);
                    Interval dayInterval = day.getDayInterval();
                    Interval dayOverlap = dayInterval.overlap(eventInterval);
                    if (dayOverlap == null) {
                        logger.debug("Event not overlapping with day");
                        if (search == Search.DURING) {
                            logger.debug("Event end day found");
                            endDay = i - 1;
                            search = Search.AFTER;
                        }
                    } else {
                        logger.debug("Event overlapping with day");
                        if (search == Search.BEFORE) {
                            logger.debug("Event start day found");
                            startDay = i;
                            search = Search.DURING;
                        }
                    }
                }

                switch (search) {
                    case BEFORE:
                        logger.warn("Day range search internal error");
                        return false;
                    case DURING:
                        endDay = 6;
                        break;
                    default:
                        /* NOTHING */
                }
                if (startDay == null) {
                    logger.warn("Start day null");
                    return false;
                }
                if (endDay == null) {
                    logger.warn("End day null");
                    return false;
                }

                boolean continues =
                        event.getInterval().getEnd().isAfter(weekEnd);

                EventWeek eventWeek =
                        new EventWeek(event, startDay, endDay, continues);
                eventWeekOverlaps.add(eventWeek);
                return true;
            }
        }

        public List<EventWeek> getEventWeekOverlaps() {
            return eventWeekOverlaps;
        }

        public void sortEvents() {
            logger.debug("Querying days for busy slots");
            for (int i1 = 0, daysLength = days.length; i1 < daysLength; i1++) {
                MonthViewDay day = getDay(i1);
                day.clearSlots();
            }

            logger.debug("Sorting event weeks");
            Collections.sort(eventWeekOverlaps, new EventWeekComparator());

            for (EventWeek current : eventWeekOverlaps) {
                Set<Integer> busySlots = new HashSet<Integer>();
                int startDay = current.getStartDay();
                int endDay = current.getEndDay();

                logger.debug("Querying days for busy slots");
                for (int i = startDay; i <= endDay; i++) {
                    MonthViewDay day = getDay(i);
                    day.fillBusySlots(busySlots);
                }

                logger.debug("Looking for first empty slot");
                int index = 0;
                boolean found = false;
                while (!found) {
                    if (busySlots.contains(index)) {
                        logger.debug("Slot {} busy", index);
                        index++;
                    } else {
                        logger.debug("Found empty slot: {}", index);
                        found = true;
                    }
                }

                logger.debug("Allocating slot");
                for (int i = startDay; i <= endDay; i++) {
                    MonthViewDay day = getDay(i);
                    day.allocateSlot(index, current);
                }
            }
        }
    }

    public class MonthViewDay extends AbstractDay {
        final List<EventWeek> slots;

        public MonthViewDay(DateMidnight dayStart, DateMidnight dayEnd) {
            super(dayStart, dayEnd);
            slots = new ArrayList<EventWeek>();
        }

        public void clearSlots() {
            slots.clear();
        }

        public void fillBusySlots(Set<Integer> busySlots) {
            for (int i = 0; i < slots.size(); i++) {
                if (slots.get(i) == null) {
                    logger.debug("Empty slot {}", i);
                } else {
                    logger.debug("Busy slot {}", i);
                    busySlots.add(i);
                }
            }
        }

        public void allocateSlot(int index, EventWeek current) {
            ensureSlotsSize(index + 1);
            slots.set(index, current);
        }

        private void ensureSlotsSize(int requiredSize) {
            while (slots.size() < requiredSize) {
                slots.add(null);
            }
        }

        public List<EventWeek> getSlots() {
            return slots;
        }

    }
}
