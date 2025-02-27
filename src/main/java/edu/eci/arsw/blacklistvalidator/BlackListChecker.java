package edu.eci.arsw.blacklistvalidator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BlackListChecker extends Thread{

    private final String ipaddress;
    private AtomicInteger ocurrencesCount;
    private final int BLACK_LIST_ALARM_COUNT;
    private HostBlacklistsDataSourceFacade skds;
    private List<Integer> blackListOcurrences;
    private final int start;
    private final int end;
    private AtomicInteger checkedListsCount;

    public BlackListChecker(String ipaddress, AtomicInteger ocurrencesCount, int BLACK_LIST_ALARM_COUNT, List<Integer> blackListOcurrences, int start, int end, AtomicInteger checkedListsCount){
        this.ipaddress = ipaddress;
        this.ocurrencesCount = ocurrencesCount;
        this.BLACK_LIST_ALARM_COUNT = BLACK_LIST_ALARM_COUNT;
        this.blackListOcurrences = blackListOcurrences;
        this.start = start;
        this.end = end;
        this.checkedListsCount = checkedListsCount;
        skds=HostBlacklistsDataSourceFacade.getInstance();
    }

    @Override
    public void run() {
        while(ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT){
            for (int i=start; (i < end) && (ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT); i++){
                checkedListsCount.addAndGet(1);
                if (skds.isInBlackListServer(i, ipaddress)){
                    blackListOcurrences.add(i);
                    ocurrencesCount.addAndGet(1);
                }
            }
            if (ocurrencesCount.get() >= BLACK_LIST_ALARM_COUNT){
                skds.reportAsNotTrustworthy(ipaddress);
            }
        }
    }
}
