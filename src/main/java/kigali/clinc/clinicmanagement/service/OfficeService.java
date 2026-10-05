package kigali.clinc.clinicmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;

@Service
public class OfficeService {
    

    @Autowired 
    private OfficeRepository officeRepo;


    public String saveOffice(Office office){

        if(officeRepo.findByOfficeNumber(office.getOfficeNumber()) != null){
            return "Office with that number "+office.getOfficeNumber()+ " already exists";
        }

        officeRepo.save(office);

        return "Office is saved Successfully";
    }

    public List<Office> getAll() {
        return officeRepo.findAll();
    }

    // C3: empty list means no appointments yet
    public List<Object[]> getBusiest() {
        return officeRepo.findBusiest(PageRequest.of(0, 1));
    }
}
