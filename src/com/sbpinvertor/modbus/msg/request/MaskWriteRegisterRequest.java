package com.sbpinvertor.modbus.msg.request;

import com.sbpinvertor.modbus.data.DataHolder;
import com.sbpinvertor.modbus.exception.ModbusNumberException;
import com.sbpinvertor.modbus.exception.ModbusProtocolException;
import com.sbpinvertor.modbus.msg.base.AbstractDataRequest;
import com.sbpinvertor.modbus.msg.base.ModbusResponse;
import com.sbpinvertor.modbus.msg.response.MaskWriteRegisterResponse;
import com.sbpinvertor.modbus.net.stream.base.ModbusInputStream;
import com.sbpinvertor.modbus.net.stream.base.ModbusOutputStream;
import com.sbpinvertor.modbus.utils.ModbusFunctionCode;

import java.io.IOException;

/**
 * Copyright (c) 2015-2016 JSC "Zavod "Invertor"
 * [http://www.sbp-invertor.ru]
 * <p/>
 * This file is part of JLibModbus.
 * <p/>
 * JLibModbus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Authors: Vladislav Y. Kochedykov, software engineer.
 * email: vladislav.kochedykov@gmail.com
 */
public class MaskWriteRegisterRequest extends AbstractDataRequest {

    private int maskAnd;
    private int maskOr;

    public MaskWriteRegisterRequest(int serverAddress) throws ModbusNumberException {
        super(serverAddress);
    }

    public MaskWriteRegisterRequest(int serverAddress, int startAddress, int maskAnd, int maskOr) throws ModbusNumberException {
        super(serverAddress, startAddress);

        setMaskAnd(maskAnd);
        setMaskOr(maskOr);
    }

    /*result = ((reg & and) | (or & !and))*/
    @Override
    public ModbusResponse getResponse(DataHolder dataHolder) throws ModbusNumberException {
        MaskWriteRegisterResponse response = new MaskWriteRegisterResponse(getServerAddress(), getStartAddress(), getMaskAnd(), getMaskOr());
        try {
            int reg = dataHolder.readHoldingRegister(getStartAddress());
            dataHolder.writeHoldingRegister(getStartAddress(), (reg & getMaskAnd()) | (getMaskOr() & (~getMaskAnd())));
        } catch (ModbusProtocolException e) {
            response.setException();
            response.setModbusExceptionCode(e.getException().getValue());
        }
        return response;
    }

    @Override
    protected void readData(ModbusInputStream fifo) throws IOException {
        setMaskAnd(fifo.readShortBE());
        setMaskOr(fifo.readShortBE());
    }

    @Override
    public void writeData(ModbusOutputStream fifo) throws IOException {
        fifo.writeShortBE(getMaskAnd());
        fifo.writeShortBE(getMaskOr());
    }

    @Override
    public ModbusFunctionCode getFunction() {
        return ModbusFunctionCode.MASK_WRITE_REGISTER;
    }

    @Override
    protected int dataSize() {
        return 4;
    }

    public int getMaskAnd() {
        return maskAnd;
    }

    public void setMaskAnd(int maskAnd) {
        this.maskAnd = maskAnd;
    }

    public int getMaskOr() {
        return maskOr;
    }

    public void setMaskOr(int maskOr) {
        this.maskOr = maskOr;
    }
}