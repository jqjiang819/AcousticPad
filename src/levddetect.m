function [ data_out, ext_out ] = levddetect( data_in, s_init, ext_in )
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
    if ~exist('s_init','var')
        levd.s(1) = 0;
    else
        levd.s(1) = s_init;
    end
    if ~exist('ext_in','var')
        levd.ext(1).data = 0;
        levd.ext(1).type = 'init';
        levd.ext(2).data = 0;
        levd.ext(2).type = 'init';
    else
        levd.ext = ext_in;
    end
    if size(ext_in) ~= [1,2]
        error('levddetect: extremas structure error.');
    end
    if size(data_in,2) > 1
        error('levddetect: input data can only be a one-dimension array');
    end
    for i = 1:length(data_in)
        % get local extremes
        switch isminmax(data_in,i)
            case -1
                if strcmp(levd.ext(2).type,'init')
                    levd.ext(2).type = 'min';
                    levd.ext(2).data = data_in(i);
                elseif strcmp(levd.ext(2).type,'min') && (data_in(i) < levd.ext(2).data)
                    levd.ext(2).data = data_in(i);
                elseif strcmp(levd.ext(2).type,'max')
                    levd.ext(1) = levd.ext(2);
                    levd.ext(2).type = 'min';
                    levd.ext(2).data = data_in(i);
                end
            case 1
                if strcmp(levd.ext(2).type,'init')
                    levd.ext(2).type = 'max';
                    levd.ext(2).data = data_in(i);
                elseif strcmp(levd.ext(2).type,'max') && (data_in(i) > levd.ext(2).data)
                    levd.ext(2).data = data_in(i);
                elseif strcmp(levd.ext(2).type,'min')
                    levd.ext(1) = levd.ext(2);
                    levd.ext(2).type = 'max';
                    levd.ext(2).data = data_in(i);
                end
        end
        % calculate static vector
        levd.s(i+1) = 0.9*levd.s(i)+0.1*getextmean(levd.ext);
    end
    ext_out = levd.ext;
    data_out = levd.s(2:end);
end

function out = isminmax(data, i)
%ISMINMAX
%  min: out = -1
%  max: out = 1
%  oth: out = 0
    % get left value
    if i == 1
        ism.out = 0;
    elseif i < 6
        ism.left = mean(data(1:i-1));
    else
        ism.left = mean(data(i-5:i-1));
    end
    % get right value
    if i == length(data)
        ism.out = 0;
    elseif i > length(data)-5
        ism.right = mean(data(i+1:end));
    else
        ism.right = mean(data(i+1:i+5));
    end
    % check center value
    if (data(i) > ism.left) && (data(i) > ism.right)
        ism.out = 1;
    elseif (data(i) < ism.left) && (data(i) < ism.right)
        ism.out = -1;
    else
        ism.out = 0;
    end
    out = ism.out;
end

function out = getextmean(ext)
%GETEXTMEAN
    if strcmp(ext(1).type,'init') || strcmp(ext(2).type,'init')
        ise.out = 0;
    else
        ise.out = (ext(1).data+ext(2).data)/2;
    end
    out = ise.out;
end