package stats;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weblogic.management.provider.DomainAccess;
import weblogic.management.provider.ManagementService;
import weblogic.management.provider.Service;
import weblogic.security.acl.internal.AuthenticatedSubject;
import weblogic.security.service.PrivilegedActions;

import com.bea.wli.config.Ref;
import com.bea.wli.monitoring.DelegatedServiceDomainMBeanImpl;
import com.bea.wli.monitoring.ResourceStatistic;
import com.bea.wli.monitoring.ResourceType;
import com.bea.wli.monitoring.ServiceResourceStatistic;
import com.bea.wli.monitoring.StatisticType;
import com.bea.wli.monitoring.StatisticValue;

public class OSBDataHandler {

	private List<OSBService> services = new ArrayList<OSBService>();
	private Map<Ref, ServiceResourceStatistic> monitoredBizServices;
	private Map<Ref, ServiceResourceStatistic> monitoredProxyServices;
	private DelegatedServiceDomainMBeanImpl serviceDomainMbean = null;
	private String serverName = null;
	private String serviceType;

	private OSBDataHandler() {
	}
	
	public static OSBDataHandler getInstance() {
		return new OSBDataHandler();
	}
	
	public List<OSBService> getOSBMonitoringData() throws Exception {
		getServiceDomainMBean();

		monitoredBizServices = getStatsForAllMonitoredBizServices();
		this.serviceType = "Business Service";
		addStatsToServiceArrayList(monitoredBizServices);

		monitoredProxyServices = getStatsForAllMonitoredProxyServices();
		this.serviceType = "Proxy Service";
		addStatsToServiceArrayList(monitoredProxyServices);

		Collections.sort(services,new Comparator<OSBService>() {
			public int compare(OSBService o1, OSBService o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		
		return services;
	}

	private void addStatsToServiceArrayList(Map<Ref, ServiceResourceStatistic> statsMap) {

		OSBValue value;
		OSBStat stat;
		OSBService service;
		OSBResource resource;

		ArrayList<OSBValue> values;
		ArrayList<OSBStat> stats;
		ArrayList<OSBResource> resources;

		if (statsMap == null) {
			return;
		}
		if (statsMap.size() == 0) {
			return;
		}

		Set<Map.Entry<Ref, ServiceResourceStatistic>> set = statsMap.entrySet();

		for (Map.Entry<Ref, ServiceResourceStatistic> mapEntry : set) {
			service = new OSBService();
			service.setName(mapEntry.getKey().getFullName());
			service.setServiceType(this.serviceType);
			ServiceResourceStatistic serviceStats = mapEntry.getValue();
			ResourceStatistic[] resStatsArray = null;
			
			try {
				resStatsArray = serviceStats.getAllResourceStatistics();
			} catch (Exception ex) {
				continue;
			} 

			resources = new ArrayList<OSBResource>();
			for (ResourceStatistic resStats : resStatsArray) {
				resource = new OSBResource();
				resource.setName(resStats.getName());
				resource.setType(resStats.getResourceType().toString());
				// Now get statistics for this resource

				StatisticValue[] statValues = resStats.getStatistics();

				stats = new ArrayList<OSBStat>();
				for (StatisticValue stValue : statValues) {
					stat = new OSBStat();
					stat.setName(stValue.getName());
					stat.setType(stValue.getType().toString());
					values = new ArrayList<OSBValue>();
					
					if (stValue.getType() == StatisticType.INTERVAL) {
						StatisticValue.IntervalStatistic is = (StatisticValue.IntervalStatistic) stValue;
						// Print interval statistics values
						value = new OSBValue();
						value.setName("Count Value");
						value.setValue(String.valueOf(is.getCount()));
						values.add(value);
						value = new OSBValue();
						value.setName("Min Value");
						value.setValue(String.valueOf(is.getMin()));
						values.add(value);
						value = new OSBValue();
						value.setName("Max Value");
						value.setValue(String.valueOf(is.getMax()));
						values.add(value);
						value = new OSBValue();
						value.setName("Sum Value");
						value.setValue(String.valueOf(is.getSum()));
						values.add(value);
						value = new OSBValue();
						value.setName("Avg Value");
						value.setValue(String.valueOf(is.getAverage()));
						values.add(value);
					} else if (stValue.getType() == StatisticType.COUNT) {
						StatisticValue.CountStatistic cs = (StatisticValue.CountStatistic) stValue;
						value = new OSBValue();
						value.setName("Count Value");
						value.setValue(String.valueOf(cs.getCount()));
						values.add(value);
					} else if (stValue.getType() == StatisticType.STATUS) {
						StatisticValue.StatusStatistic ss = (StatisticValue.StatusStatistic) stValue;
						value = new OSBValue();
						value.setName("Initial Status");
						value.setValue(String.valueOf(ss.getInitialStatus()));
						values.add(value);
						value = new OSBValue();
						value.setName("Current Status");
						value.setValue(String.valueOf(ss.getCurrentStatus()));
						values.add(value);
					}
				
					stat.setOSBValue(values);
					stats.add(stat);
				}
			
				resource.setOSBStat(stats);
				resources.add(resource);
			}
			
			service.setOSBResource(resources);
			services.add(service);
		}
	}

	private Map<Ref, ServiceResourceStatistic> getStatsForAllMonitoredBizServices() throws Exception {
		Ref[] serviceRefs = serviceDomainMbean.getMonitoredBusinessServiceRefs();

		Map<Ref,ServiceResourceStatistic> ret = new HashMap<Ref, ServiceResourceStatistic>();
		
		for (Ref ref : serviceRefs) {
			try {
				int typeFlag = ResourceType.SERVICE.value() | ResourceType.WEBSERVICE_OPERATION.value();
				HashMap<Ref, ServiceResourceStatistic> stats = serviceDomainMbean.getBusinessServiceStatistics(new Ref[]{ref}, typeFlag, serverName);
				ret.put(ref, stats.get(ref));
			} catch( Exception ex ) {
				int typeFlag = ResourceType.SERVICE.value();
				HashMap<Ref, ServiceResourceStatistic> stats = serviceDomainMbean.getBusinessServiceStatistics(new Ref[]{ref}, typeFlag, serverName);
				ret.put(ref, stats.get(ref));
			}
		}
		
		return ret;
	}

	private Map<Ref, ServiceResourceStatistic> getStatsForAllMonitoredProxyServices() throws Exception {
		Ref[] serviceRefs = serviceDomainMbean.getMonitoredProxyServiceRefs();

		Map<Ref,ServiceResourceStatistic> ret = new HashMap<Ref, ServiceResourceStatistic>();
		
		for (Ref ref : serviceRefs) {
			try {
				int typeFlag = ResourceType.SERVICE.value() | ResourceType.FLOW_COMPONENT.value() | ResourceType.WEBSERVICE_OPERATION.value();
				HashMap<Ref, ServiceResourceStatistic> stats = serviceDomainMbean.getProxyServiceStatistics(new Ref[]{ref}, typeFlag, serverName);
				ret.put(ref, stats.get(ref));
			} catch( Exception ex ) {
				int typeFlag = ResourceType.SERVICE.value() | ResourceType.FLOW_COMPONENT.value();
				HashMap<Ref, ServiceResourceStatistic> stats = serviceDomainMbean.getProxyServiceStatistics(new Ref[]{ref}, typeFlag, serverName);
				ret.put(ref, stats.get(ref));
			}
		}
		
		return ret;
	}

	@SuppressWarnings("unchecked")
	private void getServiceDomainMBean() throws Exception {
        AuthenticatedSubject kernelId = (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
        DomainAccess da = ManagementService.getDomainAccess(kernelId);

        Service[] services = da.getRootServices();
        for (Service service : services) {
        	if( "ServiceDomain".equals(service.getName()) ) {
        		serviceDomainMbean = (DelegatedServiceDomainMBeanImpl) service;
        		return;
        	}
        }
        
        throw new RuntimeException("Can't access DelegatedServiceDomainMBeanImpl");
	}

	public String getDomainName() throws Exception {
		if( serviceDomainMbean == null ) {
			getServiceDomainMBean();
		}
		return serviceDomainMbean.getDomainName();
	}
}
