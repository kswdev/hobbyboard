package com.hobbyboard.domain.account.mapper;

import com.hobbyboard.domain.account.dto.AccountDto;
import com.hobbyboard.domain.account.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
    AccountDto toAccountDto(Account account);
}
