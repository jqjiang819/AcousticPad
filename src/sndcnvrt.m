function [ out ] = sndcnvrt( respath )
%SNDCNVRT
%  This function converts the plain sound source file into a frame-based
%  sound vector to simulate the real aplication
    snd.frameSize = 3840;
    snd.pcmdata = fread(fopen(respath),'int16')/power(2,15);
    if mod(length(snd.pcmdata),snd.frameSize) ~= 0
        error('sndcnvrt: resource length error.');
    end
    for i=1:length(snd.pcmdata)/snd.frameSize
        snd.data(:,i)=snd.pcmdata(snd.frameSize*(i-1)+1:snd.frameSize*i);
    end
    out = snd.data;
end

