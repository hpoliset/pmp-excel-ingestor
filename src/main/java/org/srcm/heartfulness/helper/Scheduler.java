package org.srcm.heartfulness.helper;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.model.Coordinator;
import org.srcm.heartfulness.service.ProgramService;

@Component
public class Scheduler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);
	
	@Autowired
	ProgramService programService;
	
	@Autowired
	PmpMailHelper mailHelper;
	
	
	/*@Scheduled(cron = "0 03 15 * * *")*/
	@SuppressWarnings("unused")
	private void sendMailToCoOrdinatorsOnDailyBasis(){
		System.out.println("cron job running...");
		List<Coordinator> coOrdinatorList= programService.getAllCoOrdinatorsList();
		for (Coordinator coOrdinator : coOrdinatorList) {
			if(null != coOrdinator.getCoordinatorEmail()){
				int NonCategorizedEventCount= programService.getNonCategorizedEventsByEmail(coOrdinator.getCoordinatorEmail(),false);
				try {
					if(NonCategorizedEventCount > 0){
						mailHelper.sendMail( coOrdinator,NonCategorizedEventCount);
					}
				} catch (AddressException e) {
					LOGGER.error("Mail Sending Failed due to Invalid Address");
				} catch (MessagingException e) {
					LOGGER.error("Mail Sending Failed");
				}
			}
		}
	}


}
