package stats;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class MainServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try {
			resp.setContentType("text/plain");
			
			ServletOutputStream os = resp.getOutputStream();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String now = sdf.format(new Date());
			
			OSBDataHandler dh = OSBDataHandler.getInstance();
			
			os.println(",,,"+now+",,,,,,,,,,,,,");
			os.println(",,,"+dh.getDomainName()+",,,,,,,,,,,,,");
			os.println("Service,Type,Operation,Messages,Errors,Min(ms),Avg(ms),Max(ms),Failovers,Success(%),Failures(%),Cache Hits,Cache Hits(%),Min Throttling(ms),Avg Throttling(ms),Max Throttling(ms)");
			os.println();
			
			List<OSBService> data = dh.getOSBMonitoringData();
			
			for (OSBService svc : data) {
				String name = svc.getName();
				String type = svc.getServiceType();
				
				String prefix = name+",";
				
				if( "Business Service".equals(type) ) {
					prefix += "Biz,";
				} else if ( "Proxy Service".equals(type) ) {
					prefix += "Proxy,";
				} else {
					prefix += "?,";
				}
				
				List<OSBResource> resources = svc.getOSBResources();
				
				Collections.sort(resources, new Comparator<OSBResource>(){
					public int compare(OSBResource o1, OSBResource o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
				
				for (OSBResource res : resources) {
					String rname = res.getName();
					String rtype = res.getType();
					
					if( !"Transport".equals(rname) || !"SERVICE".equals(rtype) ) {
						continue;
					}
					
					// TVServices/AIN/BizService/RetrieveProfileService,Biz,(Overall),1289,113, 47,173,6761, 0,91.000,8.000,0,0.000,0,0,0,
					os.print(prefix);
					os.print("(Overall),");
					os.print(getStat(res.getOSBStat(),"message-count")+",");
					os.print(getStat(res.getOSBStat(),"error-count")+",");
					os.print(getStat(res.getOSBStat(),"response-time","Min Value")+",");
					os.print(getStat(res.getOSBStat(),"response-time","Avg Value")+",");
					os.print(getStat(res.getOSBStat(),"response-time","Max Value")+",");
					os.print(",,,,,");
					os.print(getStat(res.getOSBStat(),"throttling-time","Min Value")+",");
					os.print(getStat(res.getOSBStat(),"throttling-time","Avg Value")+",");
					os.print(getStat(res.getOSBStat(),"throttling-time","Max Value")+",");
					os.println();
					break;
				}

				for (OSBResource res : resources) {
					String rname = res.getName();
					String rtype = res.getType();
					
					if( "Transport".equals(rname) && "SERVICE".equals(rtype) ) {
						continue;
					}
					
					// TVServices/AIN/BizService/RetrieveProfileService,Biz,(Overall),1289,113, 47,173,6761, 0,91.000,8.000,0,0.000,0,0,0,
					os.print(prefix);
					os.print(rname+",");
					os.print(getStat(res.getOSBStat(),"message-count")+",");
					os.print(getStat(res.getOSBStat(),"error-count")+",");
					os.print(getStat(res.getOSBStat(),"elapsed-time","Min Value")+",");
					os.print(getStat(res.getOSBStat(),"elapsed-time","Avg Value")+",");
					os.print(getStat(res.getOSBStat(),"elapsed-time","Max Value")+",");
					os.print(",,,,,");
					os.print(getStat(res.getOSBStat(),"throttling-time","Min Value")+",");
					os.print(getStat(res.getOSBStat(),"throttling-time","Avg Value")+",");
					os.print(getStat(res.getOSBStat(),"throttling-time","Max Value")+",");
					os.println();
					
					// os.println(""+res);
				}
			}
			
			os.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			resp.sendError(500, e.getMessage()==null?"NPE":e.getMessage());
		}
	}

	private String getStat(List<OSBStat> osbStat, String name, String subName) {
		for (OSBStat stat : osbStat) {
			if( !name.equals(stat.getName()) ) continue;
			
			List<OSBValue> vals = stat.getOSBValue();
			for (OSBValue val : vals) {
				if( subName != null && !subName.equals(val.getName()) ) continue;
				return val.getValue();
			}
		}
		
		return "";
	}	
	
	private String getStat(List<OSBStat> osbStat, String name) {
		return getStat(osbStat, name, null);
	}

}
