function [ data_i, data_q, time ] = codetect( sndres, init_time )
%CODETECT
%  This function performs coherent phase detection, and convert the sound
%  signal into a complex baseband signal

    cod.fs = 48000;  % sample rate
    cod.fc = 18000;  % center frequency
    cod.frameSize = 3840;
    
    if size(sndres,2) > 1
        error('codetect: soundres can only be a one-dimension array');
    end
    if ~exist('init_time','var')
        init_time = 0;
    end
    
    cod.time = (init_time+1/cod.fs):(1/cod.fs):(init_time+length(sndres)/cod.fs);
    cod.y_i = sndres .* (cos(2*pi*cod.fc*cod.time))';
    cod.y_q = sndres .* (-sin(2*pi*cod.fc*cod.time))';
    
    data_i = cod.y_i;
    data_q = cod.y_q;
    time = cod.time(end);
end

