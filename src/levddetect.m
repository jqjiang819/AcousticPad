function [ data_out, ext_out ] = levddetect( data_in, ext_in )
%LEVDDETECT
%  The local extreme value detection algorithm comes from the famous 
%  Empirical Mode Decomposition (EMD) algorithm. The basic idea is that 
%  we first determine a threshold for extreme value detection and a 
%  correction coefficient, and then find the local extremes in the I/Q 
%  components separately. If the type of the new extreme is different 
%  from the last extreme in the extreme list and the difference is larger 
%  than the threshold, we add it to the extreme list. To estimate the 
%  value of the static vector by calculate the average of the last two 
%  extremes since the dynamic vector has a trace similar to circles. 
    if ~exist('ext_in','var')
        

end

