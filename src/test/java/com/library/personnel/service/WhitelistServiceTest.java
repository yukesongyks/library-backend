package com.library.personnel.service;

import com.library.personnel.dto.request.WhitelistRequest;
import com.library.personnel.dto.response.WhitelistResponse;
import com.library.personnel.entity.Whitelist;
import com.library.personnel.enums.WhitelistType;
import com.library.personnel.exception.BusinessException;
import com.library.personnel.repository.WhitelistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WhitelistServiceTest {

    @Mock
    private WhitelistRepository whitelistRepository;

    private WhitelistService whitelistService;

    @BeforeEach
    void setUp() {
        whitelistService = new WhitelistService(whitelistRepository);
    }

    @Test
    void testListWhitelistByType() {
        Whitelist whitelist = createTestWhitelist();
        when(whitelistRepository.findByType(WhitelistType.IMPORT)).thenReturn(List.of(whitelist));

        List<WhitelistResponse> result = whitelistService.listWhitelist(WhitelistType.IMPORT);

        assertEquals(1, result.size());
        assertEquals("EMP000001", result.get(0).getEmployeeId());
    }

    @Test
    void testListAllWhitelist() {
        Whitelist w1 = createTestWhitelist();
        Whitelist w2 = createTestWhitelist();
        w2.setType(WhitelistType.APPROVAL);
        w2.setEmployeeId("EMP000002");
        when(whitelistRepository.findAll()).thenReturn(List.of(w1, w2));

        List<WhitelistResponse> result = whitelistService.listWhitelist(null);

        assertEquals(2, result.size());
    }

    @Test
    void testAddToWhitelist() {
        WhitelistRequest request = new WhitelistRequest();
        request.setType(WhitelistType.IMPORT);
        request.setEmployeeId("EMP000001");
        request.setName("John Doe");

        Whitelist savedWhitelist = createTestWhitelist();
        when(whitelistRepository.existsByEmployeeIdAndType("EMP000001", WhitelistType.IMPORT)).thenReturn(false);
        when(whitelistRepository.save(any(Whitelist.class))).thenReturn(savedWhitelist);

        WhitelistResponse result = whitelistService.addToWhitelist(request);

        assertEquals("EMP000001", result.getEmployeeId());
        assertEquals(WhitelistType.IMPORT, result.getType());
    }

    @Test
    void testAddToWhitelistDuplicate() {
        WhitelistRequest request = new WhitelistRequest();
        request.setType(WhitelistType.IMPORT);
        request.setEmployeeId("EMP000001");
        request.setName("John Doe");

        when(whitelistRepository.existsByEmployeeIdAndType("EMP000001", WhitelistType.IMPORT)).thenReturn(true);

        assertThrows(BusinessException.class, () -> whitelistService.addToWhitelist(request));
        verify(whitelistRepository, never()).save(any());
    }

    @Test
    void testBatchAddToWhitelist() {
        WhitelistRequest request1 = new WhitelistRequest();
        request1.setType(WhitelistType.IMPORT);
        request1.setEmployeeId("EMP000001");
        request1.setName("John");

        WhitelistRequest request2 = new WhitelistRequest();
        request2.setType(WhitelistType.APPROVAL);
        request2.setEmployeeId("EMP000002");
        request2.setName("Jane");

        when(whitelistRepository.existsByEmployeeIdAndType(anyString(), any())).thenReturn(false);
        when(whitelistRepository.save(any(Whitelist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<WhitelistResponse> results = whitelistService.batchAddToWhitelist(List.of(request1, request2));

        assertEquals(2, results.size());
        verify(whitelistRepository, times(2)).save(any(Whitelist.class));
    }

    @Test
    void testRemoveFromWhitelist() {
        when(whitelistRepository.existsById(1L)).thenReturn(true);

        whitelistService.removeFromWhitelist(1L);

        verify(whitelistRepository).deleteById(1L);
    }

    @Test
    void testRemoveFromWhitelistNotFound() {
        when(whitelistRepository.existsById(999L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> whitelistService.removeFromWhitelist(999L));
    }

    @Test
    void testCheckWhitelist() {
        when(whitelistRepository.existsByEmployeeIdAndType("EMP000001", WhitelistType.IMPORT)).thenReturn(true);
        assertTrue(whitelistService.checkWhitelist("EMP000001", WhitelistType.IMPORT));

        when(whitelistRepository.existsByEmployeeIdAndType("EMP000002", WhitelistType.IMPORT)).thenReturn(false);
        assertFalse(whitelistService.checkWhitelist("EMP000002", WhitelistType.IMPORT));
    }

    private Whitelist createTestWhitelist() {
        Whitelist whitelist = new Whitelist();
        whitelist.setId(1L);
        whitelist.setType(WhitelistType.IMPORT);
        whitelist.setEmployeeId("EMP000001");
        whitelist.setName("John Doe");
        whitelist.setNote("Test entry");
        return whitelist;
    }
}