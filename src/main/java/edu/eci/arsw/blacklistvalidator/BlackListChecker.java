package edu.eci.arsw.blacklistvalidator;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class BlackListChecker extends Thread{

    private final String ipaddress;
    private AtomicInteger ocurrencesCount;
    private final int BLACK_LIST_ALARM_COUNT;
    private List<Integer> blackListOcurrences;
    private final int start;
    private final int end;
    private AtomicInteger checkedListsCount;
    private CountDownLatch doneSignal;

    public BlackListChecker(String ipaddress, AtomicInteger ocurrencesCount, int BLACK_LIST_ALARM_COUNT, List<Integer> blackListOcurrences, int start, int end, AtomicInteger checkedListsCount, CountDownLatch doneSignal){
        this.ipaddress = ipaddress;
        this.ocurrencesCount = ocurrencesCount;
        this.BLACK_LIST_ALARM_COUNT = BLACK_LIST_ALARM_COUNT;
        this.blackListOcurrences = blackListOcurrences;
        this.start = start;
        this.end = end;
        this.checkedListsCount = checkedListsCount;
        this.doneSignal = doneSignal;

    }

    @Override
    public void run() {
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        while(ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT){
            System.out.println("hilo: " + this.getName() + " revisando ....");
            for (int i=start; (i < end) && (ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT); i++){
                checkedListsCount.addAndGet(1);
                if (skds.isInBlackListServer(i, ipaddress)){
                    blackListOcurrences.add(i);
                    ocurrencesCount.addAndGet(1);
                }
            }
        }
        doneSignal.countDown();
        System.out.println("hilo: " + this.getName() + " termino");
    }
}
