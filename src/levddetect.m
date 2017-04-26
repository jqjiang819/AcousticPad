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
        levd.s(1) = data_in(1);
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
    if size(levd.ext) ~= [1,2]
        error('levddetect: extremas structure error.');
    end
    if size(data_in,2) > 1
        error('levddetect: input data can only be a one-dimension array');
    end
    global DATA_DBG_EXTS;
    DATA_DBG_EXTS.cnt = DATA_DBG_EXTS.cnt + 1;
    for i = 1:length(data_in)
        % get local extremes
        switch utilities.isminmax(data_in,i)
            case -1
                if strcmp(levd.ext(2).type,'init')
                    levd.ext(2).type = 'min';
                    levd.ext(2).data = data_in(i);
                    DATA_DBG_EXTS.v = [DATA_DBG_EXTS.v;data_in(i)];
                    DATA_DBG_EXTS.i = [DATA_DBG_EXTS.i;i+240*DATA_DBG_EXTS.cnt];
                elseif strcmp(levd.ext(2).type,'min') && (data_in(i) < levd.ext(2).data)
                    levd.ext(2).data = data_in(i);
                    DATA_DBG_EXTS.v(end) = data_in(i);
                    DATA_DBG_EXTS.i(end) = i+240*DATA_DBG_EXTS.cnt;
                elseif strcmp(levd.ext(2).type,'max') && (abs(data_in(i) - levd.ext(2).data) > 2e5)
                    levd.ext(1) = levd.ext(2);
                    levd.ext(2).type = 'min';
                    levd.ext(2).data = data_in(i);
                    DATA_DBG_EXTS.v = [DATA_DBG_EXTS.v;data_in(i)];
                    DATA_DBG_EXTS.i = [DATA_DBG_EXTS.i;i+240*DATA_DBG_EXTS.cnt];
                end
            case 1
                if strcmp(levd.ext(2).type,'init')
                    levd.ext(2).type = 'max';
                    levd.ext(2).data = data_in(i);
                    DATA_DBG_EXTS.v = [DATA_DBG_EXTS.v;data_in(i)];
                    DATA_DBG_EXTS.i = [DATA_DBG_EXTS.i;i+240*DATA_DBG_EXTS.cnt];
                elseif strcmp(levd.ext(2).type,'max') && (data_in(i) > levd.ext(2).data)
                    levd.ext(2).data = data_in(i);
                    DATA_DBG_EXTS.v(end) = data_in(i);
                    DATA_DBG_EXTS.i(end) = i+240*DATA_DBG_EXTS.cnt;
                elseif strcmp(levd.ext(2).type,'min') && (abs(data_in(i) - levd.ext(2).data) > 2e5)
                    levd.ext(1) = levd.ext(2);
                    levd.ext(2).type = 'max';
                    levd.ext(2).data = data_in(i);
                    DATA_DBG_EXTS.v = [DATA_DBG_EXTS.v;data_in(i)];
                    DATA_DBG_EXTS.i = [DATA_DBG_EXTS.i;i+240*DATA_DBG_EXTS.cnt];
                end
        end
        % calculate static vector
        % levd.s(i+1) = 0.9*levd.s(i)+0.1*getextmean(levd.ext);
        levd.s(i+1) = utilities.getstaticvec(data_in,levd.s,i,levd.ext);
    end
    ext_out = levd.ext;
    data_out = levd.s(2:end)';
end

