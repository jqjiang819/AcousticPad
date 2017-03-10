function [ data_out, dbuf_out, ibuf_out ] = cicdecim( data_in, dbuf_in, ibuf_in)
%CICFILT
%  This is a CIC filter designed with a section count of 3, a sampling 
%  factor of 16, and a delay of 17.
    
    cic.decf = 16;  % decimation factor
    cic.diffd = 17;  % differential delay
    cic.combbuf = zeros(1,3);
    cic.data_in = data_in;
    cic.data_out = [];
    
    if ~exist('dbuf_in','var')
        cic.delaybuf = zeros(cic.diffd,3);
    else
        cic.delaybuf = dbuf_in;
    end
    if ~exist('ibuf_in','var')
        cic.intbuf = zeros(1,3);
    else
        cic.intbuf = ibuf_in;
    end
    
    if size(cic.delaybuf) ~= [cic.diffd,3]
        error('cicdecim: delay buffer structure error.');
    end
    if size(cic.intbuf) ~= [1,3]
        error('cicdecim: integrator buffer structure error.');
    end
    
    for i = 1:length(cic.data_in)
        % integrator 1
        cic.intbuf(1,1) = cic.intbuf(1,1) + cic.data_in(i);
        % integrator 2
        cic.intbuf(1,2) = cic.intbuf(1,2) + cic.intbuf(1,1);
        % integrator 3
        cic.intbuf(1,3) = cic.intbuf(1,3) + cic.intbuf(1,2);
        if mod(i,cic.decf) == 1  % decimation
            % comb section 1
            cic.combbuf(1,1) = cic.intbuf(1,3) - cic.delaybuf(1,1);
            cic.delaybuf(1:end-1,1) = cic.delaybuf(2:end,1);
            cic.delaybuf(end,1) = cic.intbuf(1,3);
            % comb section 2
            cic.combbuf(1,2) = cic.combbuf(1,1) - cic.delaybuf(1,2);
            cic.delaybuf(1:end-1,2) = cic.delaybuf(2:end,2);
            cic.delaybuf(end,2) = cic.combbuf(1,1);
            % comb section 3
            cic.combbuf(1,3) = cic.combbuf(1,2) - cic.delaybuf(1,3);
            cic.delaybuf(1:end-1,3) = cic.delaybuf(2:end,3);
            cic.delaybuf(end,3) = cic.combbuf(1,2);
            % get output
            cic.data_out = [ cic.data_out;cic.combbuf(1,3) ];
        end
    end
    data_out = cic.data_out;
    dbuf_out = cic.delaybuf;
    ibuf_out = cic.intbuf;
end